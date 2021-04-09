import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BankStatementProcessor {
    private final List<BankTransaction> bankTransactions;

    public BankStatementProcessor (List<BankTransaction> bankTransactions) {
        this.bankTransactions = bankTransactions;
    }

    public double calculateTotal () {
        return summarizeTransactions((accumulator, transaction) -> accumulator + transaction.getAmount().getValue());
    }

    public double calculateTotalForMonth (Month month) {
        return summarizeTransactions((accumulator, transaction) -> {
            if (transaction.getDate().getMonth() == month) {
                return accumulator + transaction.getAmount().getValue();
            } else {
                return accumulator;
            }
        });
    }

    public double calculateForCategory (String category) {
        return summarizeTransactions((accumulator, transaction) -> {
            boolean isSameDescription = transaction.getDescription().equals(category);
            double transactionAmount = transaction.getAmount().getValue();
            return isSameDescription ? transactionAmount + accumulator : accumulator;
        });
    }


    public double summarizeTransactions (BankTransactionSummarizer summarizer) {
        double total = 0d;
        for (BankTransaction transaction : bankTransactions) {
            total = summarizer.summarize(total, transaction);
        }
        return total;
    }

    public List<BankTransaction> findTransactions (BankTransactionFilter filter) {
        List<BankTransaction> result = new ArrayList<>();
        for (BankTransaction transaction : bankTransactions) {
            boolean passEvaluation = filter.test(transaction);
            if (passEvaluation) {
                result.add(transaction);
            }
        }
        return result;
    }

    public double calculateAverage () {
        return calculateTotal() / bankTransactions.size();
    }

    public double calculateMin () {
        double min = 0;
        for (BankTransaction transactionMin : bankTransactions) {
            if (transactionMin.getAmount().getValue() < min) {
                min = transactionMin.getAmount().getValue();
            }
        }

        return min;
    }

    public double calculateMax () {
        double max = 0;
        for (BankTransaction transactionMax : bankTransactions) {
            if (transactionMax.getAmount().getValue() > max) {
                max = transactionMax.getAmount().getValue();
            }
        }

        return max;
    }

    public List<BankTransaction> filterByDate (String fechaInicial, String fechaFinal) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate fechaIni = LocalDate.parse(fechaInicial, formatter);
        LocalDate fechaFin = LocalDate.parse(fechaFinal, formatter);
        long numOfDaysBetween = ChronoUnit.DAYS.between(fechaIni, fechaFin.plusDays(1));
        List<BankTransaction> transactionFiltradas = new ArrayList<BankTransaction>();
        List rangeDates = IntStream.iterate(0, i -> i + 1)
                .limit(numOfDaysBetween)
                .mapToObj(i -> fechaIni.plusDays(i))
                .collect(Collectors.toList());
        for (int i = 0; i < this.bankTransactions.size(); i++) {
            boolean found = rangeDates.contains(this.bankTransactions.get(i).getDate());
            if (found) {
                transactionFiltradas.add(this.bankTransactions.get(i));
            }
        }
        return transactionFiltradas;
    }
    public double calculateMinAmount () {
        List<BankTransaction> filterAmount=filterByDate("2017-01-01", "2017-12-13");
        double min = 0;
        for (BankTransaction transactionMinAmount : filterAmount) {
            if (transactionMinAmount.getAmount().getValue() < min) {
                min = transactionMinAmount.getAmount().getValue();
                System.out.println(min);

            }
        }

        return min;
    }
    public double calculateMaxAmount () {
        List<BankTransaction> filterAmount=filterByDate("2017-01-01", "2017-12-13");
        double max = 0;
        for (BankTransaction transactionMaxAmount : filterAmount) {
            if (transactionMaxAmount.getAmount().getValue() > max) {
                max = transactionMaxAmount.getAmount().getValue();
                System.out.println(max);
            }
        }

        return max;
    }
}



