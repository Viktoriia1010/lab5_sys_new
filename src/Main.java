
import java.util.concurrent.Semaphore;

class InvalidExchangeException extends RuntimeException {//свое исключение
    public InvalidExchangeException(String message) {
        super(message);
    }
}

class ChocolateExchange {
    private int totalShokolad;
    private int totalFantik;

    private final Semaphore semaphore = new Semaphore(1);

    public ChocolateExchange(int initialShokolad, int initialFantik) {
        if (initialShokolad < 0 || initialFantik < 0) {
            throw new InvalidExchangeException(
                    "Количество шоколадок и фантиков не может быть отрицательным!"
            );
        }

        this.totalShokolad = initialShokolad;
        this.totalFantik = initialFantik;
    }

    public void processExchange(String threadName) {
        try {
            semaphore.acquire();

            int bonusFromShok = totalShokolad / 5;
            totalShokolad += bonusFromShok;
            totalFantik += totalShokolad;

            System.out.println(threadName + totalShokolad + totalFantik);

            while (totalFantik >= 3) {
                int newShok = totalFantik / 3;
                totalShokolad += newShok;
                totalFantik = (totalFantik % 3) + newShok;

                System.out.println(threadName + newShok + totalFantik);
            }

            System.out.println(totalShokolad + totalFantik);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            semaphore.release();
        }
    }
}

class ChokoThread extends Thread {
    private final ChocolateExchange exchange;

    public ChokoThread(String name, ChocolateExchange exchange) {
        super(name);
        this.exchange = exchange;
    }

    public void run() {
        exchange.processExchange(getName());
    }
}

public class Main {
    public static void main(String[] args) throws Exception {

        try {
            ChocolateExchange exchange = new ChocolateExchange(7, 1);

            ChokoThread t1 = new ChokoThread("Поток-1", exchange);
            ChokoThread t2 = new ChokoThread("Поток-2", exchange);

            t1.start();
            t2.start();

            t1.join();
            t2.join();

        } catch (InvalidExchangeException e) {
            System.out.println("Ошибка обмена: " + e.getMessage());
        }
    }
}
