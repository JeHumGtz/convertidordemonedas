import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONObject;

public class convertidordemonedas {
    public static void main(String[] args) {
        CurrencyConverter converter = new CurrencyConverter("a5e12c009c8f525d847c0b5f");
        Scanner scanner = new Scanner(System.in);

        String choice;
        do {
            displayMenu();
            choice = scanner.nextLine();

            if (choice.equals("1")) {
                displayCurrencyOptions(converter.getSupportedCurrencies());
                String fromCurrency;
                do {
                    System.out.print("Selecciona la moneda de origen: ");
                    int fromCurrencyIndex = Integer.parseInt(scanner.nextLine()) - 1;
                    fromCurrency = getCurrencyCode(converter, fromCurrencyIndex);
                    if (fromCurrency == null) {
                        System.out.println("Moneda de origen no válida. Por favor, selecciona una moneda válida.");
                    }
                } while (fromCurrency == null);

                String toCurrency;
                do {
                    System.out.print("Selecciona la moneda de destino: ");
                    int toCurrencyIndex = Integer.parseInt(scanner.nextLine()) - 1;
                    toCurrency = getCurrencyCode(converter, toCurrencyIndex);
                    if (toCurrency == null) {
                        System.out.println("Moneda de destino no válida. Por favor, selecciona una moneda válida.");
                    }
                } while (toCurrency == null);

                double amount;
                do {
                    System.out.print("Ingresa la cantidad a convertir (solo digitos): ");
                    String amountStr = scanner.nextLine();
                    if (amountStr.matches("\\d+")) {
                        amount = Double.parseDouble(amountStr);
                    } else {
                        amount = -1; // Valor inválido para continuar el bucle
                        System.out.println("Cantidad no válida. Por favor, ingresa solo dígitos.");
                    }
                } while (amount == -1);

                double convertedAmount = converter.convert(amount, fromCurrency, toCurrency);
                System.out.println(amount + " " + converter.getSupportedCurrencies().get(fromCurrency) + " (" + fromCurrency + ")" +
                        " equivale a " + convertedAmount + " " + converter.getSupportedCurrencies().get(toCurrency) + " (" + toCurrency + ")");
            } else if (choice.equals("2")) {
                System.out.println("Gracias por usar el conversor de monedas. ¡Hasta luego!");
            } else {
                System.out.println("Opción no válida. Por favor, selecciona una opción válida.");
            }
        } while (!choice.equals("2"));
    }

    public static void displayMenu() {
        System.out.println("\n===== Conversor de Monedas =====");
        System.out.println("1. Convertir moneda");
        System.out.println("2. Salir");
        System.out.print("Selecciona una opción: ");
    }

    public static void displayCurrencyOptions(Map<String, String> currencies) {
        System.out.println("Monedas disponibles:");
        int index = 1;
        for (Map.Entry<String, String> entry : currencies.entrySet()) {
            System.out.println(index + ". " + entry.getValue() + " (" + entry.getKey() + ")");
            index++;
        }
        System.out.println();
    }

    public static String getCurrencyCode(CurrencyConverter converter, int currencyIndex) {
        try {
            return (String) converter.getSupportedCurrencies().keySet().toArray()[currencyIndex];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}

class CurrencyConverter {
    private String apiKey;
    private Map<String, String> supportedCurrencies = new HashMap<>();

    public CurrencyConverter(String apiKey) {
        this.apiKey = apiKey;
        // Inicializar las monedas soportadas con sus nombres completos
        supportedCurrencies.put("ARS", "Peso Argentino");
        supportedCurrencies.put("BOB", "Boliviano");
        supportedCurrencies.put("BRL", "Real Brasileño");
        supportedCurrencies.put("CLP", "Peso Chileno");
        supportedCurrencies.put("COP", "Peso Colombiano");
        supportedCurrencies.put("USD", "Dólar Estadounidense");
        supportedCurrencies.put("MXN", "Peso Mexicano");
    }

    public double convert(double amount, String fromCurrency, String toCurrency) {
        try {
            String urlString = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/" + fromCurrency;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            StringBuilder responseBuilder = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                responseBuilder.append(output);
            }

            JSONObject response = new JSONObject(responseBuilder.toString());
            double rate = response.getJSONObject("conversion_rates").getDouble(toCurrency);
            return amount * rate;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Map<String, String> getSupportedCurrencies() {
        return supportedCurrencies;
    }
}
