package grupomoviles.quelista.igu;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import grupomoviles.quelista.R;

public class TabsFragment extends Fragment {

    private static TabLayout tabLayout;
    private static ViewPager viewPager;
    private int int_items = 3 ;
    private static FragmentPagerAdapter myAdapter;
    private int tab = 0;
    private View v = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /**
         * Inflamos fragment_tab y adjuntamos Views.
         */
        v =  inflater.inflate(R.layout.fragment_tabs,null);
        tabLayout = (TabLayout) v.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);


        /**
         * Establecemos un Adapter para el ViewPager
         */
        myAdapter = new MyAdapter(this.getChildFragmentManager());

        viewPager.setAdapter(myAdapter);

        tabLayout.setOnTabSelectedListener(null);
        viewPager.setCurrentItem(tab);
        tabLayout.setupWithViewPager(viewPager);


        // Esto es necesario debido a un bug presente en la Design Support Library 22.2.1
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
                viewPager.setCurrentItem(tab);
            }
        });

        return v;

    }

    public void setTab(int tab) {
        this.tab = tab;
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager childFragmentManager) {
            super(childFragmentManager);
        }

        /**
         * Devolvemos el fragmento correspondiente en base a la posicion
         */

        @Override
        public android.support.v4.app.Fragment getItem(int position)
        {
            switch (position){
                case 0 : return new DespensaFragment();
                case 1 : return new ListaCompraFragment();
                case 2 : return new CarritoFragment();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * Devolvemos el titulo de la pestaña en base a la posicion
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "Despensa";
                case 1 :
                    return "Lista";
                case 2 :
                    return "Carrito";
            }
            return null;
        }
    }
}
