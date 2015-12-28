package grupomoviles.quelista;

import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Stream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import grupomoviles.quelista.Database.ProductDataSource;
import grupomoviles.quelista.captureCodes.IntentCaptureActivity;

import static android.nfc.NdefRecord.createTextRecord;


public class MainActivity extends ActionBarActivity implements AppBarLayout.OnOffsetChangedListener {

    Fragment fragmentPantry;
    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DownloadImageTask task;
        task = new DownloadImageTask();
        task.execute("5449000000996", "8410297112041", "8410297170058", "8410188012092",
                "5449000009067", "8410000826937", "8410014307682", "8410014312495", "5000127281752");

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        ProductDataSource productDataSource = new ProductDataSource(this);

        productDataSource.openDatabase();
        List<Product> products = productDataSource.getAllProducts();
//        private String code;
//        private String description;
//        private String brand;
//        private String netValue;
//        private int units = 1;  //(por ejemplo, la caja de yogures contiene 8 unidades. Por defecto 1)
//
//        private String category;
//        private String subcategory;
//
//        private int stock = 0;		// (-1 == no)
//        private int minStock;
//        private int unitsToAdd;	//Unidades a añadir a la lista de la compra automáticamente (Def: 1)
//
//        private Date lastUpdate;	// (null == no añadir automáticamente)
//        private int consumeCycle;  //Período (a definir si van a ser días enteros)
//        private int consumeUnits;  //Unidades a descontar cada período
//
//        private int shoppingListUnits;	//Unidades en la lista de la compra (0 == no)
//        private int cartUnits;		//Unidades en el carrito (0 == no)
        //productDataSource.insertProduct(new Product("1", "cereales miel", "kelloks","caja 25", 4,"chocolate","salado",2,1,1,null,5,2,3,2));
        productDataSource.close();

        Stream.of(products).forEach(p -> System.out.println(p.getCode() + " " + p.getCategory() + " " + p.getSubcategory()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_pantry) {
            fragmentPantry = new FragmentPantry();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.relativeLayout, fragmentPantry)
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void scan(View view) {
        IntentCaptureActivity ica = new IntentCaptureActivity();

        //Desactiva la opción de menú reverese camera
        ica.setReverseCamera(false);
        //Desactiva el sonido al escanear (por defecto está activado)
        //ica.setBeep(false);

        ica.initScan(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (fragmentPantry != null) {
            if (verticalOffset == 0) {
                findViewById(R.id.swipeRefresh).setEnabled(true);
            } else {
                findViewById(R.id.swipeRefresh).setEnabled(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((AppBarLayout) findViewById(R.id.appBarLayout)).addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((AppBarLayout) findViewById(R.id.appBarLayout)).removeOnOffsetChangedListener(this);
    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... product) {
            FTPClient f = new FTPClient();

            try {
                Bitmap bp;
                ByteArrayOutputStream stream;
                byte[] byteArray;
                FileOutputStream outputStream;

                for (String s : product) {
                    f.connect("31.170.164.153", 21);
                    f.login("u750524270.solocarpeta", "moviles2015");
                    f.enterLocalActiveMode();
                    f.setFileType(FTP.BINARY_FILE_TYPE);

                    bp = BitmapFactory.decodeStream(f.retrieveFileStream("/" + s + ".png"));

                    if (bp != null) {
                        stream = new ByteArrayOutputStream();
                        bp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byteArray = stream.toByteArray();

                        outputStream = getApplicationContext().openFileOutput(s + ".png", Context.MODE_PRIVATE);
                        outputStream.write(byteArray);
                        outputStream.close();
                    }
                    f.disconnect();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
