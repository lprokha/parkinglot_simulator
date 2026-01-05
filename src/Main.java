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
//        System.out.println("=== Тест аналитических методов ===");
//
//        // 1. Общая сумма заработка за день 5
//        double incomeDay5 = totalIncomeForDay(cars, 5);
//        System.out.printf("Общая сумма заработка за 5-й день: %.2f USD%n", incomeDay5);
//
//        // 2. Минимальная, средняя и максимальная сумма заработка за месяц
//        double[] minAvgMax = getMinAvgMaxEarnings(cars);
//        System.out.printf("Мин/сред/макс за месяц: %.2f / %.2f / %.2f USD%n", minAvgMax[0], minAvgMax[1], minAvgMax[2]);
//
//         //3.
//        System.out.println("\n=== 10 машин, стоявших на парковке дольше всего ===");
//        List<Car> topTen = getTopTenLongestParkedCars(cars);
//        for (Car car : topTen) {
//            int totalSteps = 0;
//            for (ParkingSession s : car.getHistory()) {
//                int exit = s.exitStep;
//                if (exit == -1) exit = DAYS * STEPS_IN_DAY;
//                totalSteps += exit - s.entryStep;
//            }
//            int totalMinutes = totalSteps * MINUTES_PER_STEP;
//            System.out.printf("Машина %s: %d шагов, %d минут\n", car.getId(), totalSteps, totalMinutes);
//        }
//        //4.
//        System.out.println("=== Тест countCarsParkedLessThan30Min ===");
//        int dayToCheck = 5; // например, 5-й день
//        int shortParkedCount = countCarsParkedLessThan30Min(cars, dayToCheck);
//        System.out.printf("Количество машин, простоявших меньше 30 минут в день %d: %d\n",
//                dayToCheck, shortParkedCount);
//        //5.
//        System.out.println("=== Тест averageOccupancyPercent ===");
//        double avgOccupancy = averageOccupancyPercent(cars, dayToCheck);
//        System.out.printf("Средний процент загруженности парковки в день %d: %.2f%%\n", dayToCheck, avgOccupancy);
//        //6.
//        System.out.println("\n=== Тест: машины, побывавшие на парковке за день/час ===");
//
//// машины за целый 5-й день
//        List<Car> carsDay5 = carsPresentAtHourOrDay(cars, 5, -1);
//        System.out.println("Машины на парковке в день 5:");
//        for (Car c : carsDay5) {
//            System.out.println(c.getId());
//        }
//
//// машины на парковке в 10-й час 5-го дня (10:00 - 10:59)
//        List<Car> carsHour10 = carsPresentAtHourOrDay(cars, 5, 10);
//        System.out.println("\nМашины на парковке в день 5, час 10:");
//        for (Car c : carsHour10) {
//            System.out.println(c.getId());
//        }
//        //7.
//        System.out.println("\n=== Дни, когда машина C5 была на парковке ===");
//        List<Integer> daysC5 = getDaysCarWasParked(cars, "C5");
//        for (int d : daysC5) {
//            System.out.print(d + " ");
//        }
//        System.out.println();
    }

    public static void showMenu(List<Car> cars) {
        Scanner scanner = new Scanner(System.in);
        Canvas canvas = new Canvas(80, 20);

        while (true) {
            System.out.println("1. Показать счета за месяц");
            System.out.println("2. Список всех машин");
            System.out.println("3. Гистограмма средняя загруженность парковки");
            System.out.println("4. Гистограмма ежедневный заработок");
            System.out.println("5. Гистограмма машины < 30 минут");
            System.out.println("0. Выход");

            int choice;

            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Ошибка ввода. Пожалуйста, введите число из меню.");
                scanner.nextLine();
                continue;
            }
            switch (choice) {
                case 0 -> {
                    return;
                }

                case 1 -> calculateBills(cars);

                case 2 -> {
                    for (Car car : cars) {
                        System.out.println(car.getId());
                    }
                }

                case 3 -> {
                    double[] occupancy = new double[DAYS];
                    for (int d = 1; d <= DAYS; d++) {
                        double percent = averageOccupancyPercent(cars, d);
                        occupancy[d - 1] = percent * PARKING_SPOTS / 100.0;
                    }

                    HistogramDrawer.drawHistogram(
                            canvas,
                            occupancy,
                            "Среднее количество занятых мест по дням",
                            "*"
                    );
                }

                case 4 -> {
                    double[] income = new double[DAYS];
                    for (int d = 1; d <= DAYS; d++) {
                        income[d - 1] = totalIncomeForDay(cars, d);
                    }

                    HistogramDrawer.drawHistogram(
                            canvas,
                            income,
                            "Ежедневный заработок парковки",
                            "$"
                    );
                }

                case 5 -> {
                    double[] shortPark = new double[DAYS];
                    for (int d = 1; d <= DAYS; d++) {
                        shortPark[d - 1] = countCarsParkedLessThan30Min(cars, d);
                    }

                    HistogramDrawer.drawHistogram(
                            canvas,
                            shortPark,
                            "Машины, припаркованные < 30 минут",
                            "*"
                    );
                }

                default -> System.out.println("Неверный пункт меню");
            }
        }
    }

    public static List<Integer> getDaysCarWasParked(List<Car> cars, String carId) {
        List<Integer> days = new ArrayList<>();

        Car targetCar = null;
        for (Car c : cars) {
            if (c.getId().equals(carId)) {
                targetCar = c;
                break;
            }
        }

        if (targetCar == null) {
            return days;
        }

        for (ParkingSession session : targetCar.getHistory()) {
            int entry = session.entryStep;
            int exit = session.exitStep;
            if (exit == -1) {
                exit = DAYS * STEPS_IN_DAY;
            }

            int startDay = (entry / STEPS_IN_DAY) + 1;
            int endDay = (exit - 1) / STEPS_IN_DAY + 1;

            for (int d = startDay; d <= endDay; d++) {
                if (!days.contains(d)) {
                    days.add(d);
                }
            }
        }

        return days;
    }

    public static List<Car> carsPresentAtHourOrDay(List<Car> cars, int day, int hour) {
        List<Car> result = new ArrayList<>();

        int dayStartStep = (day - 1) * STEPS_IN_DAY;
        int stepStart;
        int stepEnd;

        if (hour == -1) {
            stepStart = dayStartStep;
            stepEnd = dayStartStep + STEPS_IN_DAY;
        } else {
            stepStart = dayStartStep + (hour * 60) / MINUTES_PER_STEP;
            stepEnd = stepStart + (60 / MINUTES_PER_STEP);
        }

        for (Car car : cars) {
            for (ParkingSession session : car.getHistory()) {
                int entry = session.entryStep;
                int exit = session.exitStep;

                if (exit == -1) {
                    exit = DAYS * STEPS_IN_DAY;
                }

                if (exit > stepStart && entry < stepEnd) {
                    result.add(car);
                    break;
                }
            }
        }

        return result;
    }

    public static double averageOccupancyPercent(List<Car> cars, int day) {
        int dayStartStep = (day - 1) * STEPS_IN_DAY;
        int dayEndStep = dayStartStep + STEPS_IN_DAY;

        int totalOccupancy = 0;
        int totalStepsCounted = 0;

        for (int step = dayStartStep; step < dayEndStep; step++) {
            int occupied = 0;
            for (Car car : cars) {
                for (ParkingSession s : car.getHistory()) {
                    int entry = s.entryStep;
                    int exit = s.exitStep;

                    if (exit == -1) {
                        exit = DAYS * STEPS_IN_DAY;
                    }

                    if (step >= entry && step < exit) {
                        occupied++;
                        break;
                    }
                }
            }
            totalOccupancy += occupied;
            totalStepsCounted++;
        }

        double averageOccupied = totalOccupancy / (1.0 * totalStepsCounted); // теперь это double
        return (averageOccupied / PARKING_SPOTS) * 100;
    }

    public static int countCarsParkedLessThan30Min(List<Car> cars, int day) {
        int count = 0;

        int dayStartStep = (day - 1) * STEPS_IN_DAY;
        int dayEndStep = dayStartStep + STEPS_IN_DAY;

        for (Car car : cars) {
            for (ParkingSession session : car.getHistory()) {
                int entry = session.entryStep;
                int exit = session.exitStep;

                if (exit == -1) {
                    exit = DAYS * STEPS_IN_DAY;
                }

                int startStep = entry;
                if (startStep < dayStartStep) {
                    startStep = dayStartStep;
                }

                int endStep = exit;
                if (endStep > dayEndStep){
                    endStep = dayEndStep;
                }

                int durationSteps = endStep - startStep;

                if (durationSteps > 0 && durationSteps * MINUTES_PER_STEP < 30) {
                    count++;
                }
            }
        }

        return count;
    }

    public static List<Car> getTopTenLongestParkedCars(List<Car> cars) {
        List<Car> carList = new ArrayList<>(cars);
        List<Integer> totalStepsList = new ArrayList<>();

        for (Car car : carList) {
            int totalSteps = 0;
            for (ParkingSession s : car.getHistory()) {
                int entry = s.entryStep;
                int exit = s.exitStep;

                if (exit == -1) {
                    exit = DAYS * STEPS_IN_DAY;
                }

                totalSteps += exit - entry;
            }
            totalStepsList.add(totalSteps);
        }

        for (int i = 0; i < totalStepsList.size(); i++) {
            for (int j = i + 1; j < totalStepsList.size(); j++) {
                if (totalStepsList.get(j) > totalStepsList.get(i)) {
                    int tempSteps = totalStepsList.get(i);
                    totalStepsList.set(i, totalStepsList.get(j));
                    totalStepsList.set(j, tempSteps);

                    Car tempCar = carList.get(i);
                    carList.set(i, carList.get(j));
                    carList.set(j, tempCar);
                }
            }
        }

        List<Car> topTen = new ArrayList<>();
        for (int i = 0; i < 10 && i < carList.size(); i++) {
            topTen.add(carList.get(i));
        }

        return topTen;
    }

    public static double[] getMinAvgMaxEarnings(List<Car> cars) {
        List<Double> totals = new ArrayList<>();

        for (Car car : cars) {
            double totalDebt = 0;

            for (ParkingSession s : car.getHistory()) {
                if (s.exitStep == -1) {
                    s.exitStep = DAYS * STEPS_IN_DAY;
                }

                int stepsParked = s.exitStep - s.entryStep;

                if (stepsParked >= 6) {
                    for (int i = s.entryStep; i < s.exitStep; i++) {
                        int stepInDay = i % STEPS_IN_DAY;
                        int hour = (stepInDay * MINUTES_PER_STEP) / 60;

                        if (hour >= 9 && hour < 21) {
                            totalDebt += 0.10;
                        }
                    }
                }
            }

            if (totalDebt > 0) {
                totals.add(totalDebt);
            }
        }

        if (totals.isEmpty()) {
            return new double[]{0, 0, 0};
        }

        double min = totals.get(0);
        double max = totals.get(0);
        double sum = 0;

        for (Double t : totals) {
            if (t < min) {
                min = t;
            }
            if (t > max) {
                max = t;
            }
            sum += t;
        }

        double avg = sum / totals.size();

        return new double[]{min, avg, max};
    }

    public static double totalIncomeForDay(List<Car> cars, int day) {
        double total = 0;

        int dayStartStep = (day - 1) * STEPS_IN_DAY;
        int dayEndStep = dayStartStep + STEPS_IN_DAY;

        for (Car car : cars) {
            for (ParkingSession session : car.getHistory()) {
                int entry = session.entryStep;
                int exit = session.exitStep;
                if (exit == -1) exit = DAYS * STEPS_IN_DAY;

                int startStep = entry;
                if (startStep < dayStartStep) {
                    startStep = dayStartStep;
                }

                int endStep = exit;
                if (endStep > dayEndStep) {
                    endStep = dayEndStep;
                }

                if (endStep - startStep >= 6) {
                    int paidSteps = 0;
                    for (int i = startStep; i < endStep; i++) {
                        int stepInDay = i % STEPS_IN_DAY;
                        int hour = (stepInDay * MINUTES_PER_STEP) / 60;

                        if (hour >= 9 && hour < 21) {
                            paidSteps++;
                        }
                    }
                    total += paidSteps * 0.10;
                }
            }
        }

        return total;
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