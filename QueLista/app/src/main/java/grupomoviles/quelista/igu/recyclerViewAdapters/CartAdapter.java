package grupomoviles.quelista.igu.recyclerViewAdapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidviewhover.BlurLayout;
import com.daimajia.swipe.SwipeLayout;

import java.util.List;

import grupomoviles.quelista.R;
import grupomoviles.quelista.logic.Cart;
import grupomoviles.quelista.logic.Product;

public class CartAdapter extends MyAdapter {

    private Cart cart;

    public CartAdapter(Context context, Cart cart) {
        super(context, Stream.of(cart.getProducts().values()).collect(Collectors.toList()));
        this.cart = cart;

        List<Product> temp = cargarBDLocal();
        if(temp!=null && !temp.isEmpty()) {
            Stream.of(temp).forEach(p -> this.onResultProductInfoActivity(p));
        }
    }

    public Cart getCart() {
        return cart;
    }

    public void swipeList() {
        items = Stream.of(cart.getProducts().values()).collect(Collectors.toList());
    }

    @Override
    public void onResultProductInfoActivity(Product product) {
        items.remove(product);
        if(cart.onResultProductInfoActivity(product))
            items.add(product);
        super.onResultProductInfoActivity(product);
    }
    @Override
    public void onResultNfcActivity(Product product) {
        Stream.of(items).forEach(i -> {
            if (i.getCode().equals(product.getCode()))
                i.setStock(i.getStock() + product.getStock());
        });
        super.onResultNfcActivity(product);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.holder_product, parent, false);

        View blur = v.findViewById(R.id.blurLayout);
        View hover = LayoutInflater.from(parent.getContext()).inflate(R.layout.hover_holder_cart, null);
        ((BlurLayout)blur).setHoverView(hover);

        ((BlurLayout)blur).addChildAppearAnimator(hover, R.id.btnShoppingList, Techniques.ZoomIn);
        ((BlurLayout)blur).addChildAppearAnimator(hover, R.id.btnPantry, Techniques.ZoomIn);
        ((BlurLayout)blur).addChildAppearAnimator(hover, R.id.btnMore, Techniques.ZoomIn);
        ((BlurLayout)blur).addChildAppearAnimator(hover, R.id.txShoppingList, Techniques.SlideInDown);
        ((BlurLayout)blur).addChildAppearAnimator(hover, R.id.txPantry, Techniques.SlideInDown);

        ((BlurLayout)blur).addChildDisappearAnimator(hover, R.id.btnShoppingList, Techniques.ZoomOut);
        ((BlurLayout)blur).addChildDisappearAnimator(hover, R.id.btnPantry, Techniques.ZoomOut);
        ((BlurLayout)blur).addChildDisappearAnimator(hover, R.id.btnMore, Techniques.ZoomOut);
        ((BlurLayout)blur).addChildDisappearAnimator(hover, R.id.txShoppingList, Techniques.SlideOutUp);
        ((BlurLayout)blur).addChildDisappearAnimator(hover, R.id.txPantry, Techniques.SlideOutUp);

        return new CartViewHolder(v, this, hover);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        Product currentItem = items.get(position);

        ((CartViewHolder)viewHolder).units.setText(String.valueOf(currentItem.getCartUnits()));
        ((CartViewHolder)viewHolder).unitsPantry.setText(String.valueOf(currentItem.getStock()));
        ((CartViewHolder)viewHolder).unitsShoppinglist.setText(String.valueOf(currentItem.getShoppingListUnits()));

        super.onBindViewHolder(viewHolder, position);
    }

    public void addToCart(Product product) {
        product.setCartUnits(Product.NOT_IN_CART + 1);
        cart.add(product);
        guardarDatosBDLocal(product);
        items.add(product);
        items = Stream.of(items).sortBy(i -> i.getDescription().charAt(0)).collect(Collectors.toList());
    }

    public void filtrar(String cadena) {
        items = Stream.of(cart.getProducts().values())
                .filter(p -> p.getDescription().trim().replace("-", "").toLowerCase().concat(" ")
                        .concat(p.getBrand().trim().toLowerCase()).concat(" ")
                        .concat(p.getNetValue().trim().toLowerCase()).contains(cadena))
                .sortBy(p -> p.getDescription() + p.getNetValue())
                .collect(Collectors.toList());
    }

    public class CartViewHolder extends MyViewHolder{

        //Hover
        private TextView unitsPantry;
        private TextView unitsShoppinglist;

        public CartViewHolder(View v, CartAdapter adapter, View hover) {
            super(v, adapter, hover);

            unitsPantry = (TextView) hover.findViewById(R.id.txPantry);
            unitsShoppinglist = (TextView) hover.findViewById(R.id.txShoppingList);
        }

        @Override
        public void onClick(View v) {
            YoYo.with(Techniques.Pulse).duration(100).playOn(v);
            switch (v.getId()) {
                case R.id.btnPlusStock:
                    units.setText(String.valueOf(product.increaseCartUnits()));
                    guardarDatosBDLocal(product);
                    break;
                case R.id.btnMinusStock:
                    if (product.getCartUnits() > 1) {
                        units.setText(String.valueOf(product.decreaseCartUnits()));
                        guardarDatosBDLocal(product);
                    }
                    else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                        dialog.setTitle("¿Desea eliminar este producto del carrito?");
                        dialog.setNegativeButton("Cancelar", null);
                        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                removeProduct();
                            }
                        });
                        dialog.show();
                    }
                    break;
                case R.id.btnDelete:
                    removeProduct();
                    break;
                default:
                    super.onClick(v);
            }
        }

        private void removeProduct() {
            ((SwipeLayout)itemView).close(false);
            blurLayout.dismissHover();
            product.setCartUnits(0);
            guardarDatosBDLocal(product);
            adapter.items.remove(product);
            cart.remove(product);
            adapter.notifyItemRemoved(getAdapterPosition());
        }
    }
}
