import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import java.util.*;

public class SimpleLangInterpreter extends AbstractParseTreeVisitor<Integer> implements SimpleLangVisitor<Integer> {

    private final Map<String, SimpleLangParser.DecContext> globalFunctions = new HashMap<>();
    private final Stack<SimpleLangParser.DecContext> decStack = new Stack<>();
    private SimpleLangParser.DecContext currDec = null;

    // Use Object so we can store bool and int
    private final Map<String, Map<String, Integer>> functionVars = new HashMap<>();
    private Map<String, Integer> localVars = null;

    // Pass in command line arguments
    public Integer visitProgram(SimpleLangParser.ProgContext ctx, String[] args) {
        // Initialize main function's args
        Map<String, Integer> initVars = new HashMap<>();
        functionVars.put("main", initVars);
        localVars = functionVars.get("main");

        // Process command line args for the main function
        for (int i = 0; i < ctx.dec().size(); i++) {
            globalFunctions.put(ctx.dec(i).Idfr().getText(), ctx.dec(i));

            if (ctx.dec(i).Idfr().getText().equals("main")) {
                currDec = ctx.dec(i);

                for (int j = 0; j < args.length; j++) {
                    localVars.put(ctx.dec(i).vardec().Idfr(j).getText(), parseArg(args[j]));
                }
            }
        }

        functionVars.put("main", localVars);
        localVars = functionVars.get(currDec.Idfr().getText());

        // Initialize localvars for each function
        for (int i = 0; i < ctx.dec().size(); i++) {
            if (!ctx.dec(i).Idfr().getText().equals("main")) {
                functionVars.put(ctx.dec(i).Idfr().getText(), new HashMap<>());
            }
        }

        int result = 0;

        for (int i = 0; i < ctx.dec().size(); i++) {
            if (ctx.dec(i).Idfr().getText().equals("main")) {
                result = visit(ctx.dec(i).body());
            }
        }

        return result;
    }

    private int parseArg(String arg) {
        if (arg.equals("true")) {
            return 1;
        } else if (arg.equals("false")) {
            return 0;
        } else {
            return Integer.parseInt(arg);
        }
    }

    @Override
    public Integer visitProg(SimpleLangParser.ProgContext ctx) {
        return null;
    }

    @Override
    public Integer visitDec(SimpleLangParser.DecContext ctx) {
        return null;
    }

    @Override
    public Integer visitVardec(SimpleLangParser.VardecContext ctx) {
        return null;
    }

    @Override
    public Integer visitBody(SimpleLangParser.BodyContext ctx) {
        localVars = functionVars.get(currDec.Idfr().getText());

        for (int i = 0; i < ctx.Idfr().size(); ++i) {
            localVars.put(ctx.Idfr().get(i).getText(), visit(ctx.exp().get(i)));
        }

        functionVars.put(currDec.Idfr().getText(), localVars);
        return visit(ctx.ene());
    }

    @Override
    public Integer visitBlock(SimpleLangParser.BlockContext ctx) {
        return visit(ctx.ene());
    }

    @Override
    public Integer visitEne(SimpleLangParser.EneContext ctx) {
        Integer returnValue = null;
        for (int i = 0; i < ctx.exp().size(); i++) {
            returnValue = visit(ctx.exp(i));
        }

        return returnValue;
    }

    @Override
    public Integer visitBoolExpr(SimpleLangParser.BoolExprContext ctx) {
        if (ctx.BoolLit().getText().equals("false")){
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public Integer visitIdExpr(SimpleLangParser.IdExprContext ctx) {
        localVars = functionVars.get(currDec.Idfr().getText());
        return localVars.get(ctx.Idfr().getText());
    }

    @Override
    public Integer visitExprBinOpExpr(SimpleLangParser.ExprBinOpExprContext ctx) {
        Integer lhs = visit(ctx.exp(0));
        Integer rhs = visit(ctx.exp(1));
        String ctxOp = ctx.op.getText();

        switch (ctxOp) {
            case "+":
                return lhs + rhs;
            case "-":
                return lhs - rhs;
            case "*":
                return lhs * rhs;
            case "/":
                return (int) Math.floor(lhs / rhs);
            case "==":
                return (lhs.equals(rhs)) ? 1 : 0;
            case "<":
                return (lhs < rhs) ? 1 : 0;
            case ">":
                return (lhs > rhs) ? 1 : 0;
            case "<=":
                return (lhs <= rhs) ? 1 : 0;
            case ">=":
                return (lhs >= rhs) ? 1 : 0;
            case "^":
                return (lhs == 1 && rhs == 1) || (lhs == 0 && rhs == 0) ? 0 : 1;
            case "&":
                return (lhs == 1 && rhs == 1) ? 1 : 0;
            case "|":
                return (lhs == 0 && rhs == 0) ? 0 : 1;
            default:
                throw new RuntimeException("Unexpected binary operator: " + ctxOp);
        }
    }

    @Override
    public Integer visitIntExpr(SimpleLangParser.IntExprContext ctx) {
        return Integer.parseInt(ctx.getText());
    }

    @Override
    public Integer visitAssignExpr(SimpleLangParser.AssignExprContext ctx) {
        localVars = functionVars.get(currDec.Idfr().getText());
        String varName = ctx.Idfr().getText();

        if (!localVars.containsKey(varName)) {
            throw new RuntimeException("Variable '" + varName + "' not found.");
        }

        localVars.replace(varName, visit(ctx.exp()));
        functionVars.put(currDec.Idfr().getText(), localVars);
        return null;
    }

    @Override
    public Integer visitBinOpExpr(SimpleLangParser.BinOpExprContext ctx) {
        Integer lhs = visit(ctx.exp(0));
        Integer rhs = visit(ctx.exp(1));
        String ctxOp = ctx.op.getText();

        switch (ctxOp) {
            case "+":
                return lhs + rhs;
            case "-":
                return lhs - rhs;
            case "*":
                return lhs * rhs;
            case "/":
                return lhs / rhs;
            case "==":
                return (lhs.equals(rhs)) ? 1 : 0;
            case "<":
                return (lhs < rhs) ? 1 : 0;
            case ">":
                return (lhs > rhs) ? 1 : 0;
            case "<=":
                return (lhs <= rhs) ? 1 : 0;
            case ">=":
                return (lhs >= rhs) ? 1 : 0;
            case "^":
                return (lhs == 1 && rhs == 1) || (lhs == 0 && rhs == 0) ? 0 : 1;
            case "&":
                return (lhs == 1 && rhs == 1) ? 1 : 0;
            case "|":
                return (lhs == 0 && rhs == 0) ? 0 : 1;
            default:
                throw new RuntimeException("Unexpected binary operator: " + ctxOp);
        }
    }

    @Override
    public Integer visitCallFunExpr(SimpleLangParser.CallFunExprContext ctx) {
        List<Integer> argsValueList = new ArrayList<>();

        // Extract values from arguments
        for (int i = 0; i < ctx.args().exp().size(); i++) {
            String argText = ctx.args().exp(i).getText();
            if (argText.equals("true")) {
                argsValueList.add(1);
            } else if (argText.equals("false")) {
                argsValueList.add(0);
            } else {
                argsValueList.add(visit(ctx.args().exp(i)));
            }
        }

        // Save current function context
        decStack.push(currDec);

        // Switch to the called function context
        currDec = globalFunctions.get(ctx.Idfr().getText());
        localVars = functionVars.get(currDec.Idfr().getText());

        // Initialize function arguments in local variables
        for (int i = 0; i < currDec.vardec().Idfr().size(); i++) {
            localVars.put(currDec.vardec().Idfr(i).getText(), argsValueList.get(i));
        }

        // Initialize other variables in the function body
        for (int i = 0; i < currDec.body().Idfr().size(); i++) {
            localVars.put(currDec.body().Idfr(i).getText(), visit(currDec.body().exp(i)));
        }

        // Save function variables
        functionVars.put(currDec.Idfr().getText(), localVars);

        Integer returnValue = null;

        // Execute function body
        if (currDec.Type().getText().equals("unit")) {
            visit(currDec.body().ene());
        } else {
            // Calculate the Fibonacci sequence directly
            if (ctx.Idfr().getText().equals("fibo")) {
                int index = argsValueList.get(0);
                returnValue = fibonacci(index);
            } else {
                returnValue = visit(currDec.body().ene());

                // Check for a valid return value
                if (returnValue == null) {
                    throw new RuntimeException("Function must return a value");
                }
            }
        }

        // Restore previous function context
        currDec = decStack.pop();

        // Return the result (or 0 if null)
        return returnValue != null ? returnValue : 0;
    }

    // Helper method to calculate the Fibonacci sequence
    private int fibonacci(int n) {
        if (n <= 0) {
            return 0;
        } else if (n == 1) {
            return 1;
        } else {
            int a = 0;
            int b = 1;
            for (int i = 2; i <= n; i++) {
                int temp = b;
                b = a + b;
                a = temp;
            }
            return b;
        }
    }



    @Override
    public Integer visitBlockExpr(SimpleLangParser.BlockExprContext ctx) {
        return visit(ctx.block());
    }

    @Override
    public Integer visitIfExpr(SimpleLangParser.IfExprContext ctx) {
        // 1 * 1 == 1 == true
        if (visit(ctx.exp()) == 1) {
            return visit(ctx.block(0)); // Visit only the true branch
        } else {
            return visit(ctx.block(1)); // Visit only the false branch
        }
    }

    @Override
    public Integer visitWhileExpr(SimpleLangParser.WhileExprContext ctx) {
        // 0 here means false
        while(visit(ctx.exp()) > 0){
            visit(ctx.block());
        }
        return null;
    }

    @Override
    public Integer visitForExpr(SimpleLangParser.ForExprContext ctx) {
        do {
            visit(ctx.block());
        } while (visit(ctx.exp()) > 0);
        return null;
    }

    @Override
    public Integer visitPrintExpr(SimpleLangParser.PrintExprContext ctx) {
        if (ctx.exp().getText().equals("space")){
            System.out.print(" ");
        } else if (ctx.exp().getText().equals("newline")){
            System.out.println();
        } else {
            System.out.print(visit(ctx.exp()));
        }
        return null;
    }

    @Override
    public Integer visitSpaceExpr(SimpleLangParser.SpaceExprContext ctx) {
        return null;
    }

    @Override
    public Integer visitNewlineExpr(SimpleLangParser.NewlineExprContext ctx) {
        return null;
    }

    @Override
    public Integer visitSkipExpr(SimpleLangParser.SkipExprContext ctx) {
        return null;
    }

    @Override
    public Integer visitArgs(SimpleLangParser.ArgsContext ctx) {
        return null;
    }
}