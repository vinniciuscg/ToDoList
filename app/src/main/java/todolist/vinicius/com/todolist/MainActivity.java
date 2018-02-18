package todolist.vinicius.com.todolist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText caixaTexto;
    private Button botaoSalvar;
    private ListView listaExibicao;
    private SQLiteDatabase listaTarefas;

    private ArrayList<String> listaItens;
    private ArrayAdapter<String> adaptadorItens;
    private ArrayList<Integer> listaIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        caixaTexto = findViewById(R.id.textoEntradaId);
        botaoSalvar = findViewById(R.id.botaoSalvarId);
        listaExibicao = findViewById(R.id.listaId);

        try {
            //Criando o banco
            listaTarefas = openOrCreateDatabase("listaTarefas", MODE_PRIVATE, null);
            listaTarefas.execSQL("CREATE TABLE IF NOT EXISTS tarefas(id INTEGER PRIMARY KEY AUTOINCREMENT, descricao VARCHAR)");

            botaoSalvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Adicionando tarefa ao banco
                    String tarefa = caixaTexto.getText().toString();
                    salvar(tarefa);
                }
            });

            listaExibicao.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    listaTarefas.execSQL("DELETE FROM tarefas WHERE id =" + listaIds.get(i));
                    Toast.makeText(MainActivity.this, "Tarefa removida!", Toast.LENGTH_SHORT).show();
                    recuperar();
                }
            });

            recuperar();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void salvar(String texto){

        try{
            if(texto.equals("")){
                Toast.makeText(this, "Nada digitado", Toast.LENGTH_SHORT).show();
            }else{
                listaTarefas.execSQL("INSERT INTO tarefas(descricao) VALUES ('" + texto + "')");
                Toast.makeText(this, "Tarefa Salva", Toast.LENGTH_SHORT).show();
                recuperar();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void recuperar(){

        listaItens = new ArrayList<>();
        listaIds = new ArrayList<>();
        adaptadorItens = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_2,
                android.R.id.text2,
                listaItens);
        listaExibicao.setAdapter(adaptadorItens);

        try{
            //Recuperando informação do banco
            Cursor cursor = listaTarefas.rawQuery("SELECT * FROM tarefas ORDER BY id DESC", null);

            int indiceId = cursor.getColumnIndex("id");
            int indiceDesc = cursor.getColumnIndex("descricao");

            cursor.moveToFirst();
            while(cursor != null){
                listaIds.add(Integer.parseInt(cursor.getString(indiceId)));
                listaItens.add(cursor.getString(indiceId) +" - "+ cursor.getString(indiceDesc));
                cursor.moveToNext();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected static void hideKeyboard(Context context, View editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
