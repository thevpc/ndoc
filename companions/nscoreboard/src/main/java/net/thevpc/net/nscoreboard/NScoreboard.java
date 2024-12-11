/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package net.thevpc.net.nscoreboard;

import net.thevpc.net.nscoreboard.engine.NScoreboardFrame;
import net.thevpc.net.nscoreboard.model.NScore;
import net.thevpc.net.nscoreboard.util.Colors;

/**
 * @author vpc
 */
public class NScoreboard {

    public static void main(String[] args) {
        new NScoreboardFrame(new net.thevpc.net.nscoreboard.model.NScoreboard()
                .setTitle("Avidea Student Challenge 2024")
                .setScores(new NScore("dhiaeddine_raha1", "cpp", 4 * 1000 + (3000 - 103)),
                        new NScore("mohamed_gharab", "cpp", 4 * 1000 + (3000 - 2321)),
                        new NScore("wissem_yousfi", "python/print", 1 * 1000 + (3000 - 2371)),
                        new NScore("bensassiamine275", "bot/java", 4 * 1000 + (3000 - 92) - 3000),
                        new NScore("ahmedaziz_rezgu1", "cpp", 4 * 1000 + (3000 - 101)),
                        new NScore("master_ganso", "python", 4 * 1000 + (3000 - 2179)),
                        new NScore("yasserchouket011", "cpp", 3 * 1000 + (3000 - 210))
                )
                .setIcon(NScoreboard.class.getResource("/avidea.png"))
                .withColors(Colors.PALLET_1)
                .setFps(60)
                .setDurationSeconds(60)
                .setSortSpeed(2)
        );
    }
}
