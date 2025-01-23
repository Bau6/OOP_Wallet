import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PersonalFinanceManager {

    private static final String DATA_FILE = "wallet.dat";

    private static Map<String, Wallet> wallets = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        PersonalFinanceManager manager = new PersonalFinanceManager();

        manager.loadWallets();

        while (true) {
            System.out.println("1. Вход");
            System.out.println("2. Создать нового пользователя");
            System.out.println("3. Выход");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    manager.login();
                    break;
                case 2:
                    manager.createUser();
                    break;
                case 3:
                    manager.saveWallets();
                    System.exit(0);
            }
        }
    }

    private void loadWallets() {
        try {
            String data = new String(Files.readAllBytes(Paths.get(DATA_FILE)));
            String[] lines = data.split("\n");

            for (String line : lines) {
                String[] parts = line.split(",");
                String username = parts[0];
                double balance = Double.parseDouble(parts[1]);
                Wallet wallet = new Wallet(balance);
                wallets.put(username, wallet);
            }
        } catch (IOException e) {
            // Ignore exception if file does not exist
        }
    }

    private static void saveWallets() {
        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            for (Map.Entry<String, Wallet> entry : wallets.entrySet()) {
                String username = entry.getKey();
                Wallet wallet = entry.getValue();
                writer.write(username + "," + wallet.getBalance() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void login() {
        System.out.println("Введите имя пользователя:");
        String username = scanner.next();
        System.out.println("Введите пароль:");
        String password = scanner.next();

        Wallet wallet = wallets.get(username);
        if (wallet == null) {
            System.out.println("Неверные учетные данные");
            return;
        }

        WalletMenu walletMenu = new WalletMenu(wallet);
        walletMenu.run();
    }

    private void createUser() {
        System.out.println("Введите имя пользователя:");
        String username = scanner.next();
        System.out.println("Введите пароль:");
        String password = scanner.next();

        Wallet wallet = new Wallet(0);
        wallets.put(username, wallet);

        System.out.println("Пользователь создан успешно");
    }

    private static class WalletMenu {

        private final Wallet wallet;
        private final Scanner scanner;

        private WalletMenu(Wallet wallet) {
            this.wallet = wallet;
            this.scanner = new Scanner(System.in);
        }

        public void run() {
            while (true) {
                System.out.println("1. Добавить доход");
                System.out.println("2. Добавить расход");
                System.out.println("3. Установить бюджет");
                System.out.println("4. Показать баланс");
                System.out.println("5. Показать транзакции");
                System.out.println("6. Выйти");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        addIncome();
                        break;
                    case 2:
                        addExpense();
                        break;
                    case 3:
                        setBudget();
                        break;
                    case 4:
                        showBalance();
                        break;
                    case 5:
                        showTransactions();
                        break;
                    case 6:
                        logout();
                        break;
                }
            }
        }

        private void addIncome() {
            System.out.println("Введите сумму:");
            double amount = scanner.nextDouble();
            System.out.println("Введите категорию:");
            String category = scanner.next();

            wallet.addIncome(amount, category);

            System.out.println("Доход добавлен успешно");
        }

        private void addExpense() {
            System.out.println("Введите сумму:");
            double amount = scanner.nextDouble();
            System.out.println("Введите категорию:");
            String category = scanner.next();

            wallet.addExpense(amount, category);

            System.out.println("Расход добавлен успешно");
        }

        private void setBudget() {
            System.out.println("Введите категорию:");
            String category = scanner.next();
            System.out.println("Введите бюджет:");
            double budget = scanner.nextDouble();

            wallet.setBudget(category, budget);

            System.out.println("Бюджет установлен успешно");
        }

        private void showBalance() {
            System.out.println("Текущий баланс: " + wallet.getBalance());
        }

        private void showTransactions() {
            for (Transaction transaction : wallet.getTransactions()) {
                System.out.println(transaction);
            }
        }

        private void logout() {
            PersonalFinanceManager.saveWallets();
            System.exit(0);
        }
    }

    private static class Wallet {

        private double balance;
        private Map<String, Double> budgets = new HashMap<>();
        private List<Transaction> transactions = new ArrayList<>();

        public Wallet(double balance) {
            this.balance = balance;
        }

        public double getBalance() {
            return balance;
        }

        public void addIncome(double amount, String category) {
            balance += amount;
            transactions.add(new Transaction(amount, category, true));
        }

        public void addExpense(double amount, String category) {
            balance -= amount;
            transactions.add(new Transaction(amount, category, false));
        }

        public void setBudget(String category, double budget) {
            budgets.put(category, budget);
        }

        public List<Transaction> getTransactions() {
            return transactions;
        }

        @Override
        public String toString() {
            return "Кошелек{" +
                    "баланс=" + balance +
                    ", бюджеты=" + budgets +
                    ", транзакции=" + transactions +
                    '}';
        }
    }

    private static class Transaction {

        private double amount;
        private String category;
        private boolean isIncome;

        public Transaction(double amount, String category, boolean isIncome) {
            this.amount = amount;
            this.category = category;
            this.isIncome = isIncome;
        }

        @Override
        public String toString() {
            return "Транзакция{" +
                    "сумма=" + amount +
                    ", категория='" + category + '\'' +
                    ", доход=" + isIncome +
                    '}';
        }
    }
}
