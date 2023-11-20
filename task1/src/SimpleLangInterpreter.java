import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

public class SimpleLangInterpreter extends AbstractParseTreeVisitor<Integer> implements SimpleLangVisitor<Integer> {

    private final Map<String, SimpleLangParser.DecContext> global_func = new HashMap<>();
    private final Stack<Map<String, Integer>> frames = new Stack<>();

    public Integer visitProgram(SimpleLangParser.ProgContext ctx, String[] args) {
        for (int i = 0; i < ctx.dec().size(); ++i) {
            SimpleLangParser.DecContext dec = ctx.dec(i);
            global_func.put(dec.Idfr().getText(), dec);
        }

        SimpleLangParser.DecContext main = global_func.get("main");

        Map<String, Integer> newFrame = new HashMap<>();
        for (String arg : args) {
            if (arg.equals("true")) {
                newFrame.put(main.Idfr().getText(), 1);
            } else if (arg.equals("false")) {
                newFrame.put(main.Idfr().getText(), 0);
            } else {
                newFrame.put(main.Idfr().getText(), Integer.parseInt(arg));
            }
        }

        frames.push(newFrame);
        return visit(main);
    }

    @Override public Integer visitProg(SimpleLangParser.ProgContext ctx)
    {

        throw new RuntimeException("Should not be here!");

    }

    @Override public Integer visitDec(SimpleLangParser.DecContext ctx)
    {

        Integer returnValue = visit(ctx.body());
        frames.pop();
        return returnValue;

    }

    @Override
    public Integer visitVardec(SimpleLangParser.VardecContext ctx) {
        return null;
    }

    @Override public Integer visitBody(SimpleLangParser.BodyContext ctx) {

        Integer returnValue = null;

        for (int i = 0; i < ctx.exp().size(); ++i) {
            SimpleLangParser.ExpContext exp = ctx.exp(i);
            returnValue = visit(exp);
        }
        return returnValue;

    }

    @Override
    public Integer visitBlock(SimpleLangParser.BlockContext ctx) {
        Integer returnValue = null;
        SimpleLangParser.EneContext eneContext = ctx.ene();

        for (int i = 0; i < eneContext.exp().size(); ++i) {
            SimpleLangParser.ExpContext exp = eneContext.exp(i);
            returnValue = visit(exp);
        }

        return returnValue;
    }

    @Override
    public Integer visitEne(SimpleLangParser.EneContext ctx) {
        return null;
    }

    @Override
    public Integer visitBoolExpr(SimpleLangParser.BoolExprContext ctx) {
        return null;
    }

    @Override public Integer visitAssignExpr(SimpleLangParser.AssignExprContext ctx)
    {

        SimpleLangParser.ExpContext rhs = ctx.exp();
        frames.peek().replace(ctx.Idfr().getText(), visit(rhs));
        return null;

    }

    @Override public Integer visitBinOpExpr(SimpleLangParser.BinOpExprContext ctx) {
        SimpleLangParser.ExpContext operand1 = ctx.exp(0);
        Integer oprnd1 = visit(operand1);
        SimpleLangParser.ExpContext operand2 = ctx.exp(1);
        Integer oprnd2 = visit(operand2);

        return switch (((TerminalNode) (ctx.BinOP().getChild(0))).getSymbol().getType()) {
            case SimpleLangParser.Eq -> (Objects.equals(oprnd1, oprnd2)) ? 1 : 0;
            case SimpleLangParser.Less -> (oprnd1 < oprnd2) ? 1 : 0;
            case SimpleLangParser.LessEq -> (oprnd1 <= oprnd2) ? 1 : 0;
            case SimpleLangParser.Greater -> (oprnd1 > oprnd2) ? 1 : 0;
            case SimpleLangParser.GreaterEq -> (oprnd1 >= oprnd2) ? 1 : 0;
            case SimpleLangParser.Plus -> oprnd1 + oprnd2;
            case SimpleLangParser.Minus -> oprnd1 - oprnd2;
            case SimpleLangParser.Times -> oprnd1 * oprnd2;
            default -> throw new RuntimeException("Shouldn't be here - wrong binary operator.");
        };
    }

    @Override
    public Integer visitCallFunExpr(SimpleLangParser.CallFunExprContext ctx) {
        return null;
    }

    @Override public Integer visitBlockExpr(SimpleLangParser.BlockExprContext ctx) {
        return visit(ctx.block());
    }

    @Override public Integer visitIfExpr(SimpleLangParser.IfExprContext ctx)
    {

        SimpleLangParser.ExpContext cond = ctx.exp();
        Integer condValue = visit(cond);
        if (condValue > 0) {

            SimpleLangParser.BlockContext thenBlock = ctx.block(0);
            return visit(thenBlock);

        } else {

            SimpleLangParser.BlockContext elseBlock = ctx.block(1);
            return visit(elseBlock);

        }

    }

    @Override
    public Integer visitWhileExpr(SimpleLangParser.WhileExprContext ctx) {
        return null;
    }

    @Override
    public Integer visitForExpr(SimpleLangParser.ForExprContext ctx) {
        return null;
    }

    @Override public Integer visitPrintExpr(SimpleLangParser.PrintExprContext ctx) {

        SimpleLangParser.ExpContext exp = ctx.exp();

        if (((TerminalNode) exp.getChild(0)).getSymbol().getType() == SimpleLangParser.Space) {

            System.out.print(" ");

        } else if (((TerminalNode) exp.getChild(0)).getSymbol().getType() == SimpleLangParser.NewLine) {

            System.out.println();

        } else {

            System.out.print(visit(exp));

        }

        return null;

    }

    @Override public Integer visitSpaceExpr(SimpleLangParser.SpaceExprContext ctx) {
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

    @Override public Integer visitIdExpr(SimpleLangParser.IdExprContext ctx)
    {
        return frames.peek().get(ctx.Idfr().getText());
    }

    @Override public Integer visitIntExpr(SimpleLangParser.IntExprContext ctx)
    {

        return Integer.parseInt(ctx.IntLit().getText());

    }
}