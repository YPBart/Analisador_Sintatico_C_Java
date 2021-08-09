/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizadores;

import java.util.ArrayList;
import tokens.LexicalError;
import tokens.Out;
import tokens.Token;

/**
 *
 * @author Dell-User
 */
public class AnalisadorSintatico {

    //Para o analisador Lexico
    private AnalisadorLexico analex;

    //Token Actual
    private Out tokens;

    //contador de Erros
    public int count = 0;

    //Instanciando o tokens
    Token t;

    //Instanciar o construtor e receber o Analisador Lexico
    public AnalisadorSintatico(AnalisadorLexico analexic) {
        this.analex = analexic;
        //Chamar o primeiro não-terminal da gramatica
        goal();

    }

    //Função pra verificar o proximo token e Emitir Mensagens de Erro Sintático
    private void verificarProximoToken(Token token) {
        //Verificar se o TOkens actual é igual ao que está a ser passado
        if (tokens.getToken() == token) {
            //Chamar o proximo token
            tokens = analex.nextToken();
        } else {
            System.out.println("[Linha(" + tokens.getLinha() + ")]: Esperava-se [" + converterToken(token) + "] mas foi encontrado [" + converterToken(tokens.getToken()) + "]");
            //Acrescentar ao número de erros
            count++;
        }
    }

    public void goal() {
        tokens = analex.nextToken();
        program();
        verificarProximoToken(t.TOKEN_EOF);

        //Verificar a quantidade de Erros
        if (count == 0) {
            System.out.println("Compilação realizada com sucesso.");
        } else {
            System.err.println();
            System.err.println("--------------------------");
            System.err.println("Total de Erros: "+count+";");
            System.err.println("--------------------------");
            System.err.println("Compilado com erros...");

        }

    }

    private void program() {
        externs();
    }

    private void externs() {
        if (tokens.getToken() == t.TOKEN_ID || tokens.getToken() == t.TOKEN_CHAR
                || tokens.getToken() == t.TOKEN_FLOAT || tokens.getToken() == t.TOKEN_INT) {
            extern();
            externs();
        } else {
            vazio();
        }
    }

    private void extern() {
        if (tokens.getToken() == t.TOKEN_ID) {
            verificarProximoToken(t.TOKEN_ID);
            func();
        } else {
            type();
            restoextern();
        }
    }

    private void restoextern() {
        if (tokens.getToken() == t.TOKEN_ID) {
            verificarProximoToken(t.TOKEN_ID);
            restoextern2();
        } else {
            verificarProximoToken(t.TOKEN_ASTERISCO);
            verificarProximoToken(t.TOKEN_ID);
            restovars();

        }
    }

    private void restoextern2() {
        if (tokens.getToken() == t.TOKEN_ABRE_PARENTESES) {
            func();
        } else {
            restodclr();
            restovars();
        }
    }

    private void var() {
        type();
        dclr();
        restovars();
    }

    private void restovars() {
        if (tokens.getToken() == t.TOKEN_VIRGULA) {
            verificarProximoToken(t.TOKEN_VIRGULA);
            dclr();
            restovars();
        } else {
            //vazio();
            verificarProximoToken(t.TOKEN_PONTO_E_VIRGULA);
        }
    }

    private void type() {
        if (tokens.getToken() == t.TOKEN_INT) {
            verificarProximoToken(t.TOKEN_INT);
        }
        if (tokens.getToken() == t.TOKEN_FLOAT) {
            verificarProximoToken(t.TOKEN_FLOAT);
        }
        if (tokens.getToken() == t.TOKEN_CHAR) {
            verificarProximoToken(t.TOKEN_CHAR);
        }
    }

    private void dclr() {
        if (tokens.getToken() == t.TOKEN_ID) {
            verificarProximoToken(t.TOKEN_ID);
            restodclr();
        } else {
            verificarProximoToken(t.TOKEN_ASTERISCO);
            verificarProximoToken(t.TOKEN_ID);
        }
    }

    private void restodclr() {
        if (tokens.getToken() == t.TOKEN_ABRE_PARENTESES_RECTOS) {
            verificarProximoToken(t.TOKEN_ABRE_PARENTESES_RECTOS);
            restodclr2();
        } else {
            vazio();
        }
    }

    private void restodclr2() {
        if (tokens.getToken() == t.TOKEN_FECHA_PARENTESES_RECTOS) {
            verificarProximoToken(t.TOKEN_FECHA_PARENTESES_RECTOS);
        } else {
            verificarProximoToken(t.TOKEN_DIGITO_INT);
            verificarProximoToken(t.TOKEN_FECHA_PARENTESES_RECTOS);
        }
    }

    private void dcls() {
        if (tokens.getToken() == t.TOKEN_CHAR || tokens.getToken() == t.TOKEN_FLOAT
                || tokens.getToken() == t.TOKEN_INT) {
            var();
            //verificarProximoToken(t.PTOVIR);
            dcls();
        } else {
            vazio();
        }
    }

    private void func() {
        fargs();
        verificarProximoToken(t.TOKEN_ABRE_CHAVETAS);
        dcls();
        stmts();
        verificarProximoToken(t.TOKEN_FECHA_CHAVETAS);
    }

    private void fargs() {
        verificarProximoToken(t.TOKEN_ABRE_PARENTESES);
        restofargs();
    }

    private void restofargs() {
        if (tokens.getToken() == t.TOKEN_FECHA_PARENTESES) {
            verificarProximoToken(t.TOKEN_FECHA_PARENTESES);
        } else {
            args();
            verificarProximoToken(t.TOKEN_FECHA_PARENTESES);
        }
    }

    private void args() {
        type();
        dclr();
        restoargs();
    }

    private void restoargs() {
        if (tokens.getToken() == t.TOKEN_VIRGULA) {
            verificarProximoToken(t.TOKEN_VIRGULA);
            args();
        } else {
            vazio();
        }
    }

    private void stmts() {
        if (tokens.getToken() == t.TOKEN_IF || tokens.getToken() == t.TOKEN_WHILE
                || tokens.getToken() == t.TOKEN_FOR || tokens.getToken() == t.TOKEN_RETURN
                || tokens.getToken() == t.TOKEN_BREAK || tokens.getToken() == t.TOKEN_CONTINUE
                || tokens.getToken() == t.TOKEN_PONTO_E_VIRGULA || tokens.getToken() == t.TOKEN_DIFERENTE
                || tokens.getToken() == t.TOKEN_E_COMERCIAL || tokens.getToken() == t.TOKEN_ASTERISCO
                || tokens.getToken() == t.TOKEN_MENOS || tokens.getToken() == t.TOKEN_MAIS
                || tokens.getToken() == t.TOKEN_TIL || tokens.getToken() == t.TOKEN_INCREMENTO
                || tokens.getToken() == t.TOKEN_DECREMENTAL || tokens.getToken() == t.TOKEN_ID
                || tokens.getToken() == t.TOKEN_ABRE_PARENTESES || tokens.getToken() == t.TOKEN_DIGITO_FLOAT
                || tokens.getToken() == t.TOKEN_DIGITO_INT || tokens.getToken() == t.TOKEN_STRING
                || tokens.getToken() == t.TOKEN_ABRE_CHAVETAS) {
            stmt();
            stmts();
        } else {
            vazio();
        }
    }

    private void stmt() {
        if (tokens.getToken() == t.TOKEN_IF) {
            verificarProximoToken(t.TOKEN_IF);
            verificarProximoToken(t.TOKEN_ABRE_PARENTESES);
            expro();
            verificarProximoToken(t.TOKEN_FECHA_PARENTESES);
            stmt();
            restoif();
        } else if (tokens.getToken() == t.TOKEN_WHILE) {
            verificarProximoToken(t.TOKEN_WHILE);
            verificarProximoToken(t.TOKEN_ABRE_PARENTESES);
            expro();
            verificarProximoToken(t.TOKEN_FECHA_PARENTESES);
            stmt();
        } else if (tokens.getToken() == t.TOKEN_FOR) {
            verificarProximoToken(t.TOKEN_FOR);
            verificarProximoToken(t.TOKEN_ABRE_PARENTESES);
            expro();
            verificarProximoToken(t.TOKEN_PONTO_E_VIRGULA);
            expro();
            verificarProximoToken(t.TOKEN_PONTO_E_VIRGULA);
            expro();
            verificarProximoToken(t.TOKEN_FECHA_PARENTESES);
            stmt();
        } else if (tokens.getToken() == t.TOKEN_RETURN) {
            verificarProximoToken(t.TOKEN_RETURN);
            restoreturn();
        } else if (tokens.getToken() == t.TOKEN_BREAK) {
            verificarProximoToken(t.TOKEN_BREAK);
            verificarProximoToken(t.TOKEN_PONTO_E_VIRGULA);
        } else if (tokens.getToken() == t.TOKEN_CONTINUE) {
            verificarProximoToken(t.TOKEN_CONTINUE);
            verificarProximoToken(t.TOKEN_PONTO_E_VIRGULA);
        } else if (tokens.getToken() == t.TOKEN_ABRE_CHAVETAS) {
            block();
        } else if (tokens.getToken() == t.TOKEN_PONTO_E_VIRGULA) {
            verificarProximoToken(t.TOKEN_PONTO_E_VIRGULA);
        } else {
            expr();
            verificarProximoToken(t.TOKEN_PONTO_E_VIRGULA);
        }
    }

    private void restoif() {
        if (tokens.getToken() == t.TOKEN_ELSE) {
            verificarProximoToken(t.TOKEN_ELSE);
            stmt();
        } else {
            vazio();
        }
    }

    private void restoreturn() {
        if (tokens.getToken() == t.TOKEN_PONTO_E_VIRGULA) {
            verificarProximoToken(t.TOKEN_PONTO_E_VIRGULA);
        } else {
            expr();
            verificarProximoToken(t.TOKEN_PONTO_E_VIRGULA);
        }
    }

    private void block() {
        verificarProximoToken(t.TOKEN_ABRE_CHAVETAS);
        stmts();
        verificarProximoToken(t.TOKEN_FECHA_CHAVETAS);
    }

    private void lval() {
        if (tokens.getToken() == t.TOKEN_ID) {
            verificarProximoToken(t.TOKEN_ID);
            restolval();
        } else {
            verificarProximoToken(t.TOKEN_ASTERISCO);
            verificarProximoToken(t.TOKEN_ID);
        }
    }

    private void restolval() {
        if (tokens.getToken() == t.TOKEN_ABRE_PARENTESES_RECTOS) {
            verificarProximoToken(t.TOKEN_ABRE_PARENTESES_RECTOS);
            expr();
            verificarProximoToken(t.TOKEN_FECHA_PARENTESES_RECTOS);
        } else {
            vazio();
        }
    }

    private void expr() {
        restoexpr(or());
    }

    private void restoexpr(boolean lval) {
        if (tokens.getToken() == t.TOKEN_IGUAL) {
            if (lval) {
                verificarProximoToken(t.TOKEN_IGUAL);
                expr();
            } else {
                error_semantico("Erro de Atribuição");
            }
        } else {
            vazio();
        }
    }

    private boolean or() {
        boolean e1 = and();
        boolean e2 = restoor();
        return e1 && e2;
    }

    private boolean restoor() {
        if (tokens.getToken() == t.TOKEN_OU) {
            verificarProximoToken(t.TOKEN_OU);
            or();
            return false;
        } else {
            vazio();
            return true;
        }
    }

    private boolean and() {
        boolean e1 = not();
        boolean e2 = restoand();
        return e1 && e2;
    }

    private boolean restoand() {
        if (tokens.getToken() == t.TOKEN_E_COMERCIAL_LOGICO) {
            verificarProximoToken(t.TOKEN_E_COMERCIAL_LOGICO);
            and();
            return false;
        } else {
            vazio();
            return true;
        }
    }

    private boolean not() {
        if (tokens.getToken() == t.TOKEN_NEGACAO) {
            verificarProximoToken(t.TOKEN_NEGACAO);
            not();
            return false;
        } else {
            return cfator();
        }
    }

    private boolean cfator() {
        boolean e1 = orbin();
        boolean e2 = restocfator();
        return e1 && e2;
    }

    private boolean restocfator() {
        if (tokens.getToken() == t.TOKEN_IGUAL_LOGICO) {
            verificarProximoToken(t.TOKEN_IGUAL_LOGICO);
            orbin();
            return false;
        } else if (tokens.getToken() == t.TOKEN_DIFERENTE) {
            verificarProximoToken(t.TOKEN_DIFERENTE);
            orbin();
            return false;
        } else if (tokens.getToken() == t.TOKEN_MENOS_IGUAL) {
            verificarProximoToken(t.TOKEN_MENOS_IGUAL);
            orbin();
            return false;
        } else if (tokens.getToken() == t.TOKEN_MAIOR_IGUAL) {
            verificarProximoToken(t.TOKEN_MAIOR_IGUAL);
            orbin();
            return false;
        } else if (tokens.getToken() == t.TOKEN_MENOR) {
            verificarProximoToken(t.TOKEN_MENOR);
            orbin();
            return false;
        } else if (tokens.getToken() == t.TOKEN_MAIOR) {
            verificarProximoToken(t.TOKEN_MAIOR);
            orbin();
            return false;
        } else {
            vazio();
            return true;
        }
    }

    private boolean orbin() {
        boolean e1 = xorbin();
        boolean e2 = restoorbin();
        return e1 && e2;
    }

    private boolean restoorbin() {
        if (tokens.getToken() == t.TOKEN_BARRA) {
            verificarProximoToken(t.TOKEN_BARRA);
            xorbin();
            restoorbin();
            return false;
        } else {
            vazio();
            return true;
        }
    }

    private boolean xorbin() {
        boolean r1 = andbin();
        boolean r2 = restoxorbin();
        return r1 && r2;
    }

    private boolean restoxorbin() {
        if (tokens.getToken() == t.TOKEN_CHAPEU) {
            verificarProximoToken(t.TOKEN_CHAPEU);
            andbin();
            restoxorbin();
            return false;
        } else {
            vazio();
            return true;
        }
    }

    private boolean andbin() {
        boolean r1 = rola();
        boolean r2 = restoandbin();
        return r1 && r2;
    }

    private boolean restoandbin() {
        if (tokens.getToken() == t.TOKEN_E_COMERCIAL) {
            verificarProximoToken(t.TOKEN_E_COMERCIAL);
            rola();
            restoandbin();
            return false;
        } else {
            vazio();
            return true;
        }
    }

    private boolean rola() {
        boolean r1 = soma();
        boolean r2 = restorola();
        return r1 && r2;
    }

    private boolean restorola() {
        if (tokens.getToken() == t.TOKEN_MENOR_MENOR) {
            verificarProximoToken(t.TOKEN_MENOR_MENOR);
            soma();
            restorola();
            return false;
        } else if (tokens.getToken() == t.TOKEN_MAIOR_MAIOR) {
            verificarProximoToken(t.TOKEN_MAIOR_MAIOR);
            soma();
            restorola();
            return false;
        } else {
            vazio();
            return true;
        }
    }

    private boolean soma() {
        boolean r1 = mult();
        boolean r2 = restosoma();
        return r1 && r2;
    }

    private boolean restosoma() {
        if (tokens.getToken() == t.TOKEN_MAIS) {
            verificarProximoToken(t.TOKEN_MAIS);
            mult();
            restosoma();
            return false;
        } else if (tokens.getToken() == t.TOKEN_MENOS) {
            verificarProximoToken(t.TOKEN_MENOS);
            mult();
            restosoma();
            return false;
        } else {
            vazio();
            return true;
        }
    }

    private boolean mult() {
        boolean r1 = ender();
        boolean r2 = restomult();
        return r1 && r2;
    }

    private boolean restomult() {
        if (tokens.getToken() == t.TOKEN_ASTERISCO) {
            verificarProximoToken(t.TOKEN_ASTERISCO);
            boolean r1 = ender();
            boolean r2 = restomult();
            return r1 && r2;
        } else if (tokens.getToken() == t.TOKEN_DIVISAO) {
            verificarProximoToken(t.TOKEN_DIVISAO);
            ender();
            restomult();
            return false;
        } else if (tokens.getToken() == t.TOKEN_PERCENTAGEM) {
            verificarProximoToken(t.TOKEN_PERCENTAGEM);
            ender();
            restomult();
            return false;
        } else {
            vazio();
            return true;
        }
    }

    private boolean ender() {
        if (tokens.getToken() == t.TOKEN_E_COMERCIAL) {
            verificarProximoToken(t.TOKEN_E_COMERCIAL);
            lval();
            return false;
        } else if (tokens.getToken() == t.TOKEN_ASTERISCO) {
            boolean error;
            verificarProximoToken(t.TOKEN_ASTERISCO);
            error = ender();
            if (!error) {
                error_semantico("Referenciação inválida");
            }
            return error;
        } else {
            return uno();
        }
    }

    private boolean uno() {
        if (tokens.getToken() == t.TOKEN_MENOS) {
            verificarProximoToken(t.TOKEN_MENOS);
            uno();
            return false;
        } else if (tokens.getToken() == t.TOKEN_MAIS) {
            verificarProximoToken(t.TOKEN_MAIS);
            uno();
            return false;
        } else {
            return notbin();
        }
    }

    private boolean notbin() {
        if (tokens.getToken() == t.TOKEN_TIL) {
            verificarProximoToken(t.TOKEN_TIL);
            notbin();
            return false;
        } else {
            return incpre();
        }
    }

    private boolean incpre() {
        if (tokens.getToken() == t.TOKEN_INCREMENTO) {
            verificarProximoToken(t.TOKEN_INCREMENTO);
            lval();
            return true;
        } else if (tokens.getToken() == t.TOKEN_DECREMENTAL) {
            verificarProximoToken(t.TOKEN_DECREMENTAL);
            lval();
            return true;
        } else {
            return incpos();
        }
    }

    private boolean incpos() {
        boolean retorno = fator();
        restoincpos();
        return retorno;
    }

    private void restoincpos() {
        if (tokens.getToken() == t.TOKEN_DECREMENTAL) {
            verificarProximoToken(t.TOKEN_DECREMENTAL);
        } else if (tokens.getToken() == t.TOKEN_INCREMENTO) {
            verificarProximoToken(t.TOKEN_INCREMENTO);
        } else {
            vazio();
        }
    }

    private boolean fator() {
        if (tokens.getToken() == t.TOKEN_STRING) {
            verificarProximoToken(t.TOKEN_STRING);
            return false;
        } else if (tokens.getToken() == t.TOKEN_ABRE_PARENTESES) {
            verificarProximoToken(t.TOKEN_ABRE_PARENTESES);
            expr();
            verificarProximoToken(t.TOKEN_FECHA_PARENTESES);
            return false;
        } else if (tokens.getToken() == t.TOKEN_DIGITO_INT) {
            verificarProximoToken(t.TOKEN_DIGITO_INT);
            return false;
        } else if (tokens.getToken() == t.TOKEN_DIGITO_FLOAT) {
            verificarProximoToken(t.TOKEN_DIGITO_FLOAT);
            return false;
        } else {
            verificarProximoToken(t.TOKEN_ID);
            return restofator1();
        }
    }

    private boolean restofator1() {
        if (tokens.getToken() == t.TOKEN_ABRE_PARENTESES_RECTOS) {
            verificarProximoToken(t.TOKEN_ABRE_PARENTESES_RECTOS);
            expr();
            verificarProximoToken(t.TOKEN_FECHA_PARENTESES_RECTOS);
            return true;
        } else if (tokens.getToken() == t.TOKEN_ABRE_PARENTESES) {
            verificarProximoToken(t.TOKEN_ABRE_PARENTESES);
            restofator2();
            return false;
        } else {
            vazio();
            return true;
        }
    }

    private void restofator2() {
        if (tokens.getToken() == t.TOKEN_FECHA_PARENTESES) {
            verificarProximoToken(t.TOKEN_FECHA_PARENTESES);
        } else {
            exprs();
            verificarProximoToken(t.TOKEN_FECHA_PARENTESES);
        }
    }

    private void exprs() {
        expr();
        restoexprs();
    }

    private void restoexprs() {
        if (tokens.getToken() == t.TOKEN_VIRGULA) {
            verificarProximoToken(t.TOKEN_VIRGULA);
            exprs();
        } else {
            vazio();
        }
    }

    private void expro() {
        if (tokens.getToken() == t.TOKEN_NEGACAO || tokens.getToken() == t.TOKEN_E_COMERCIAL
                || tokens.getToken() == t.TOKEN_ASTERISCO || tokens.getToken() == t.TOKEN_MENOS
                || tokens.getToken() == t.TOKEN_MAIS || tokens.getToken() == t.TOKEN_TIL
                || tokens.getToken() == t.TOKEN_INCREMENTO || tokens.getToken() == t.TOKEN_DECREMENTAL
                || tokens.getToken() == t.TOKEN_ID || tokens.getToken() == t.TOKEN_ABRE_PARENTESES
                || tokens.getToken() == t.TOKEN_DIGITO_INT || tokens.getToken() == t.TOKEN_DIGITO_FLOAT
                || tokens.getToken() == t.TOKEN_STRING || tokens.getToken() == t.TOKEN_E_COMERCIAL_LOGICO || tokens.getToken() == t.TOKEN_OU) {
            expr();
        } else {
            vazio();
        }
    }

    //Vazio dos estados 
    private void vazio() {
        return;
    }

    //Erros Semanticos
    private void error_semantico(String erro) {
        LexicalError lex = new LexicalError(tokens.getLinha(), "Erro Semantico: " + erro);
        listaErros.add(lex);
    }

    //Função pra converter um TOken Enum em String
    public String converterToken(Token a) {

        if (a == t.TOKEN_PONTO_E_VIRGULA) {
            return ";";
        } else if (a == t.TOKEN_VIRGULA) {
            return ",";
        } else if (a == t.TOKEN_CHAR) {
            return "char";
        } else if (a == t.TOKEN_FLOAT) {
            return "float";
        } else if (a == t.TOKEN_INT) {
            return "int";
        } else if (a == t.TOKEN_ID) {
            return "ID";
        } else if (a == t.TOKEN_DIGITO_INT) {
            return "Dígito Inteiro";
        } else if (a == t.TOKEN_DIGITO_FLOAT) {
            return "Dígito de Ponto Flutuante";
        } else if (a == t.TOKEN_ABRE_CHAVETAS) {
            return "{";
        } else if (a == t.TOKEN_FECHA_CHAVETAS) {
            return "}";
        } else if (a == t.TOKEN_ABRE_PARENTESES) {
            return "(";
        } else if (a == t.TOKEN_FECHA_PARENTESES) {
            return ")";
        } else if (a == t.TOKEN_ABRE_PARENTESES_RECTOS) {
            return "[";
        } else if (a == t.TOKEN_FECHA_PARENTESES_RECTOS) {
            return "]";
        } else if (a == t.TOKEN_IF) {
            return "if";
        } else if (a == t.TOKEN_ELSE) {
            return "else";
        } else if (a == t.TOKEN_WHILE) {
            return "while";
        } else if (a == t.TOKEN_FOR) {
            return "for";
        } else if (a == t.TOKEN_RETURN) {
            return "return";
        } else if (a == t.TOKEN_BREAK) {
            return "break";
        } else if (a == t.TOKEN_CONTINUE) {
            return "continue";
        } else if (a == t.TOKEN_ASTERISCO) {
            return "*";
        } else if (a == t.TOKEN_IGUAL) {
            return "=";
        } else if (a == t.TOKEN_BARRA) {
            return "*";
        } else if (a == t.TOKEN_E_COMERCIAL) {
            return "&";
        } else if (a == t.TOKEN_CHAPEU) {
            return "^";
        } else if (a == t.TOKEN_MAIOR_MAIOR) {
            return ">>";
        } else if (a == t.TOKEN_MENOR_MENOR) {
            return "<<";
        } else if (a == t.TOKEN_MAIS) {
            return "+";
        } else if (a == t.TOKEN_MENOS) {
            return "-";
        } else if (a == t.TOKEN_DIVISAO) {
            return "/";
        } else if (a == t.TOKEN_PERCENTAGEM) {
            return "%";
        } else if (a == t.TOKEN_INCREMENTO) {
            return "++";
        } else if (a == t.TOKEN_DECREMENTAL) {
            return "--";
        } else if (a == t.TOKEN_STRING) {
            return "Cadeia de Caracteres";
        } else if (a == t.TOKEN_IGUAL_LOGICO) {
            return "==";
        } else if (a == t.TOKEN_DIFERENTE) {
            return "!=";
        } else if (a == t.TOKEN_MAIOR) {
            return ">";
        } else if (a == t.TOKEN_MAIOR_IGUAL) {
            return ">=";
        } else if (a == t.TOKEN_MENOR) {
            return "<";
        } else if (a == t.TOKEN_MENOR_IGUAL) {
            return "<=";
        } else if (a == t.TOKEN_OU) {
            return "||";
        } else if (a == t.TOKEN_E_COMERCIAL_LOGICO) {
            return "&&";
        } else if (a == t.TOKEN_NEGACAO) {
            return "!";
        } else if (a == t.TOKEN_TIL) {
            return "~";
        } else {
            return "";
        }

    }
    //Lista de Erros Sintaticos e Semanticos
    public static ArrayList<LexicalError> listaErros = new ArrayList<>();
}
