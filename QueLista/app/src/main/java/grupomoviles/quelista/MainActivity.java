package grupomoviles.quelista;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    TextView tx;
    List<String> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tx = (TextView) findViewById(R.id.titulo);

        lista = new ArrayList<String>();

        lista.add("Hola");
        lista.add("Hol");
        lista.add("Hoa");
        lista.add("ola");
        lista.add("Hoasla");
        lista.add("333");

        //Ejemplo de Stream y funcion lambda
        Stream.of(lista).filter(s -> s.isEmpty());

        //Linea de prueba para dani

        //Linea de prueba
        Stream.of(lista).filter(s -> s.length() == 3);

        //Retornar la lista (.collect())
        lista = Stream.of(lista).filter(s -> s.length() == 3).collect(Collectors.toList());


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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    public void prueba(View view){
//
//        //Prueba de boton que ejecuta una funcion lambda
//
//        //Por alguna razon no muestra el contenido de cada elemento del array, solamente el ultimo
//
//        tx.setText("");
//
//        Stream.of(lista).forEach((s) -> {
//
//                    RelativeLayout layout2 = (RelativeLayout) findViewById(R.id.layoutPrincipal);
//                    TextView tx2 = new TextView(this);
//                    tx2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//
//                    tx2.setText(s);
//                    layout2.addView(tx2);
//
//
//                    tx.setText(s);
//
//                    //tx.setText(tx.getText() + s + "\n");
//                }
//        );
//
//
//       // Intent intent = new Intent(MainActivity.this,ActivityPruebas.class);
//       //startActivity(intent);
//    }

    public void prueba(View view) {
        Intent intent = new Intent(MainActivity.this,ActivityPruebas.class);
        startActivity(intent);
    }
}
