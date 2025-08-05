package net.thevpc.ntexup.examples;

import net.thevpc.ntexup.engine.util.NTxUtilsText;

public class Test2 {
    public static void main(String[] args) {
        String a="\n" +
                "                    - Hello ##World##\n" +
                "                    - Hello ###World###\n" +
                "                    - Hello ####World####\n" +
                "                ";
        System.out.println("\"----------\n"
                + NTxUtilsText.trimBloc(a)
                +"\n--------------------\""
        );
    }
}
