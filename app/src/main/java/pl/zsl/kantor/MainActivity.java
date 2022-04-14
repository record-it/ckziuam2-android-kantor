package pl.zsl.kantor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.zsl.kantor.model.CurrencyRate;
import pl.zsl.kantor.model.CurrencyRatesTable;

public class MainActivity extends AppCompatActivity {
    static final String API_URL_A = "https://api.nbp.pl/api/exchangerates/tables/A/last?format=json";
    static final String API_URL_B = "https://api.nbp.pl/api/exchangerates/tables/B/last?format=json";
    static final String API_URL_C = "https://api.nbp.pl/api/exchangerates/tables/C/last?format=json";
    ExecutorService executor = Executors.newSingleThreadExecutor();
    List<CurrencyRatesTable> currenciesRatesTables;
    Button mCalcBtn;
    EditText mInputSourceValue;
    TextView mOutputTargetValue;
    Spinner mSourceCurrency;
    Spinner mTargetCurrency;
    Spinner mTableRates;
    Handler handler;
    String currentUrl = API_URL_A;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler(Looper.getMainLooper());
        mCalcBtn = findViewById(R.id.calcBtn);
        mOutputTargetValue = findViewById(R.id.targetCurrencyOutput);
        mInputSourceValue = findViewById(R.id.sourceCurrencyInput);
        mSourceCurrency = findViewById(R.id.sourceCurrencySpinner);
        mTargetCurrency = findViewById(R.id.targetCurrencySpinner);
        mTableRates = findViewById(R.id.tableSpinner);
        updateTanble();
        mCalcBtn.setOnClickListener(e -> {
            String valueText = mInputSourceValue.getText().toString();
            double amount = Double.parseDouble(valueText);
            CurrencyRate source = (CurrencyRate) mSourceCurrency.getSelectedItem();
            CurrencyRate target = (CurrencyRate) mTargetCurrency.getSelectedItem();
            double output = amount * source.getMid() / target.getMid();
            mOutputTargetValue.setText(String.format("%.2f", output));
        });

        mTableRates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //zmien currentUrl zgodnie z wybraną tabelą w spinnerze
                switch (position){
                    case 0:
                        currentUrl = API_URL_A;
                        break;
                    case 1:
                        currentUrl = API_URL_B;
                }
                updateTanble();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateTanble() {
        executor.execute(() -> {
            currenciesRatesTables = fetchCurrencies(currentUrl);
            handler.post(() -> {
                ArrayAdapter<CurrencyRate> adapter = new ArrayAdapter<>(
                        getBaseContext(),
                        android.R.layout.simple_spinner_item,
                        currenciesRatesTables.get(0).getRates()
                );
                mSourceCurrency.setAdapter(adapter);
                mTargetCurrency.setAdapter(adapter);
            });
        });
    }

    private List<CurrencyRatesTable> fetchCurrencies(String urlString){
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String jsonStr  = reader.readLine();
            ObjectMapper objectMapper = new ObjectMapper();
            List<CurrencyRatesTable> tables = objectMapper.readValue(jsonStr, new TypeReference<List<CurrencyRatesTable>>() {});
            CurrencyRate pln = new CurrencyRate("złoty polski", "PLN", 1);
            tables.get(0).getRates().add(0, pln);
            return tables;
        } catch (IOException e) {
            //Toast.makeText(getBaseContext(),"Nie można połączyć się z siecią!!", Toast.LENGTH_LONG);
            e.printStackTrace();
        }
        return null;
    }

}