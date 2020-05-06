package com.itbam.gabrielvillacrez.peopletracker.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itbam.gabrielvillacrez.peopletracker.R;
import com.itbam.gabrielvillacrez.peopletracker.objects.Person;

import java.util.List;

public class AdapterPersonalizado extends BaseAdapter
{
    private final List<Person> pessoas;

    private final Activity act;


    public AdapterPersonalizado(List<Person> pessoas, Activity act)
    {
        this.pessoas = pessoas;
        this.act = act;
    }

    @Override
    public int getCount() {
        return pessoas.size();
    }

    @Override
    public Object getItem(int position) {
        return pessoas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = act.getLayoutInflater().inflate(R.layout.lista_personalizada, parent,false);

        TextView nome = (TextView) view.findViewById(R.id.lista_personalizada_nome);
        TextView hora = (TextView) view.findViewById(R.id.lista_personalizada_hora);
        ImageView imagem = (ImageView) view.findViewById(R.id.lista_personalizada_imagem);


        Person pessoa = pessoas.get(position);

        String nomePessoa = pessoa.getNome();
        String dataEhora = pessoa.getDataPassagem();

        nome.setText(nomePessoa);
        hora.setText(dataEhora);

        switch(nomePessoa)
        {
            case "Jose Junior":
                nome.setText("José Junior");
                imagem.setImageResource(R.drawable.jose);
                break;
            case "Charles Santos":
                imagem.setImageResource(R.drawable.charles);
                break;
            case "Gilberto Novaes":
                imagem.setImageResource(R.drawable.giba);
                break;
            case "Rejane Novaes":
                imagem.setImageResource(R.drawable.rejane2);
                break;
            case "Clemilton Gomes":
                imagem.setImageResource(R.drawable.clemiltongomes);
                break;
            case "Daniele Santos":
                imagem.setImageResource(R.drawable.daniele);
                break;
            case "Cassio Achao":
                nome.setText("Cássio Achão");
                imagem.setImageResource(R.drawable.cassio);
                break;
            case "Luan Souza":
                imagem.setImageResource(R.drawable.luan);
                break;
            case "Carlos Fonseca":
                imagem.setImageResource(R.drawable.carlos);
                break;

            default:
                    imagem.setImageResource(R.drawable.logo);

        }


        return view;
    }
}
