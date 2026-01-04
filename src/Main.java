import java.util.*;

public class Main {
    static final int TOTAL_CARS = 50;
    static final int PARKING_SPOTS = 20;
    static final int DAYS = 30;
    static final int MINUTES_PER_STEP = 5;
    static final int STEPS_IN_DAY = (24 * 60) / MINUTES_PER_STEP;

    public static void main(String[] args) {
        List<Car> cars = new ArrayList<>();
        for (int i = 1; i <= TOTAL_CARS; i++) {
            cars.add(new Car("C" + i));
        }

        int currentParkedCount = 0;
        Random random = new Random();

        for (int step = 0; step < DAYS * STEPS_IN_DAY; step++) {
            for (Car car : cars) {
                double chance = random.nextDouble();

                if (car.getCurrentState() == Car.State.IN_MOTION) {
                    if (chance < 0.001 && currentParkedCount < PARKING_SPOTS) {
                        car.setCurrentState(Car.State.PARKED);
                        ParkingSession session = new ParkingSession();
                        session.entryStep = step;
                        car.getHistory().add(session);
                        currentParkedCount++;
                    }
                } else {
                    if (chance < 0.01) {
                        car.setCurrentState(Car.State.IN_MOTION);
                        List<ParkingSession> history = car.getHistory();
                        history.get(history.size() - 1).exitStep = step;
                        currentParkedCount--;
                    }
                }
            }
        }
        showMenu(cars);
    }

    public static void showMenu(List<Car> cars) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Показать счета за месяц");
            System.out.println("2. Список всех машин");
            System.out.println("0. Выход");

            int choice = scanner.nextInt();
            if (choice == 0) break;

            if (choice == 1) {
                calculateBills(cars);
            }
        }
    }

    public static void calculateBills(List<Car> cars) {
        System.out.println("время с 09:00 до 21:00:");
        for (Car car : cars) {
            double totalDebt = 0;
            for (ParkingSession s : car.getHistory()) {
                if (s.exitStep == -1) s.exitStep = DAYS * STEPS_IN_DAY;

                int paidSteps = 0;
                for (int i = s.entryStep; i < s.exitStep; i++) {
                    int stepInDay = i % STEPS_IN_DAY;
                    int hour = (stepInDay * MINUTES_PER_STEP) / 60;

                    if (hour >= 9 && hour < 21) {
                        paidSteps++;
                    }
                }

                if ((s.exitStep - s.entryStep) >= 2) {
                    totalDebt += paidSteps * 0.10;
                }
            }
            if (totalDebt > 0) {
                System.out.printf("Машина %s: %.2f USD\n", car.getId(), totalDebt);
            }
        }
    }
}