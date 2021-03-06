package MathParser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import MathParser.operations.Operation;
import MathParser.simbols.DigitsType;

/**
 * The type Expression.
 */
public class Expression {

    private Expression() {
    }

    /**
     * Implementation of <a href="https://en.wikipedia.org/wiki/Reverse_Polish_notation">Reverse_Polish_notation</a>
     */
    private static String sortingStation(String expression,
                                         Map<String, Integer> operations,
                                         String leftBracket,
                                         String rightBracket) {
        if (expression == null || expression.length() == 0)
            throw new IllegalStateException("Expression isn't specified.");
        if (operations == null || operations.isEmpty())
            throw new IllegalStateException("Operations aren't specified.");
        // Выходная строка, разбитая на "символы" - операции и операнды..
        List<String> out = new ArrayList<>();
        // Стек операций.
        Stack<String> stack = new Stack<>();

        // Удаление пробелов из выражения.
        expression = expression.replace(" ", "");

        // Множество "символов", не являющихся операндами (операции и скобки).
        Set<String> operationSymbols = new HashSet<>(operations.keySet());
        operationSymbols.add(leftBracket);
        operationSymbols.add(rightBracket);

        // Индекс, на котором закончился разбор строки на прошлой итерации.
        int index = 0;
        // Признак необходимости поиска следующего элемента.
        boolean findNext = true;
        while (findNext) {
            int nextOperationIndex = expression.length();
            String nextOperation = "";
            // Поиск следующего оператора или скобки.
            for (String operation : operationSymbols) {
                int i = expression.indexOf(operation, index);
                if (i >= 0 && i < nextOperationIndex) {
                    nextOperation = operation;
                    nextOperationIndex = i;
                }
            }
            // Оператор не найден.
            if (nextOperationIndex == expression.length()) {
                findNext = false;
            } else {
                // Если оператору или скобке предшествует операнд, добавляем его в выходную строку.
                if (index != nextOperationIndex) {
                    out.add(expression.substring(index, nextOperationIndex));
                }
                // Обработка операторов и скобок.
                // Открывающая скобка.
                if (nextOperation.equals(leftBracket)) {
                    stack.push(nextOperation);
                }
                // Закрывающая скобка.
                else if (nextOperation.equals(rightBracket)) {
                    while (!stack.peek().equals(leftBracket)) {
                        out.add(stack.pop());
                        if (stack.empty()) {
                            throw new IllegalArgumentException(
                                    "Unmatched brackets");
                        }
                    }
                    stack.pop();
                }
                // Операция.
                else {
                    while (!stack.empty() && !stack.peek().equals(leftBracket) &&
                            (operations.get(nextOperation) >= operations.get(
                                    stack.peek()))) {
                        out.add(stack.pop());
                    }
                    stack.push(nextOperation);
                }
                index = nextOperationIndex + nextOperation.length();
            }
        }
        // Добавление в выходную строку операндов после последнего операнда.
        if (index != expression.length()) {
            out.add(expression.substring(index));
        }
        // Пробразование выходного списка к выходной строке.
        while (!stack.empty()) {
            out.add(stack.pop());
        }
        StringBuilder result = new StringBuilder();
        if (!out.isEmpty())
            result.append(out.remove(0));
        while (!out.isEmpty())
            result.append(" ").append(out.remove(0));

        return result.toString();
    }

    /**
     * Sorting station string.
     *
     * @param expression the expression
     * @param operations the operations
     * @return the string
     */
    public static String sortingStation(String expression,
                                        Map<String, Integer> operations) {
        return sortingStation(expression, operations, "(", ")");
    }

    /**
     * Calculate expression big decimal.
     *
     * @param expression the expression
     * @return the big decimal
     */
    public static BigDecimal calculateExpression(String expression) {
        String rpn = sortingStation(expression, Operation.getOperations());
        StringTokenizer tokenizer = new StringTokenizer(rpn, " ");
        Stack<BigDecimal> stack = new Stack<>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            // Операнд.
            if (!Operation.getOperations().keySet().contains(token)) {
                stack.push(DigitsType.interpret(token));
            } else {
                BigDecimal operand2 = stack.pop();
                BigDecimal operand1 = stack.empty() ? BigDecimal.ZERO : stack.pop();
                stack.push(Operation.doCalc(token, operand1, operand2));
            }
        }
        if (stack.size() != 1)
            throw new IllegalArgumentException("Expression syntax error.");
        return stack.pop();
    }

    /**
     * About exp.
     *
     * @param expression the expression
     */
    public static void aboutExp(String expression) {
        System.out.println("Инфиксная нотация:         " + expression);
        String rpn = sortingStation(expression, Operation.getOperations());
        System.out.println("Обратная польская нотация: " + rpn);
        System.out.println("\tРезультат " + calculateExpression(expression));
    }
}
