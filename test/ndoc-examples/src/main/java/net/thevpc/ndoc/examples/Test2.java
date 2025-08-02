package net.thevpc.ndoc.examples;

import net.thevpc.ndoc.engine.util.NDocUtilsText;

public class Test2 {
    public static void main(String[] args) {
        String a="\n" +
                "                    - Hello ##World##\n" +
                "                    - Hello ###World###\n" +
                "                    - Hello ####World####\n" +
                "                ";
        System.out.println("\"----------\n"
                + NDocUtilsText.trimBloc(a)
                +"\n--------------------\""
        );
    }
}
