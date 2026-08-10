package ExtraUtilities.ui;

import arc.Core;
import arc.util.Log;
import mindustry.Vars;
import mindustry.core.GameState;

public class OSLog {

    public static void show(String name, Object format) {
        String ft = Core.bundle.format("eu-oslog-" + name + ".title", format);
        String fm = Core.bundle.format("eu-oslog-" + name + ".massage", format);
        try {
            ft = ft.replace("\"", "\\\"").replace("'", "\\'");
            fm = fm.replace("\"", "\\\"").replace("'", "\\'");

            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb = null;

            if (os.contains("win")) {
                // Windows
                pb = new ProcessBuilder(
                        "mshta", "javascript:new ActiveXObject('WScript.Shell').Popup('" + fm + "',0,'" + ft + "',16);close();"
                );
            } else if (os.contains("mac")) {
                // macOS
                pb = new ProcessBuilder(
                        "osascript", "-e",
                        "display dialog \"" + fm + "\" with title \"" + ft + "\" buttons {\"OK\"} default button \"OK\""
                );
            } else if (os.contains("linux")) {
                // Linux
                try {
                    pb = new ProcessBuilder("zenity", "--error", "--title=" + ft, "--text=" + fm);
                } catch (Exception e) {
                    pb = new ProcessBuilder("xmessage", "-title", ft, fm);
                }
            }

            if (pb != null) {
                pb.start();
            }
        } catch (Exception e) {
            // 任何系统不兼容时，输出日志并使用游戏内弹窗兜底
            Log.info("系统弹窗失败，使用游戏内提示: @: @", ft, fm);
            try {
                String finalMessage = fm;
                Vars.state.set(GameState.State.paused);
                new arc.scene.ui.Dialog(ft) {{
                    cont.add(finalMessage).pad(20);
                    buttons.button("OK", this::hide).size(100, 40);
                }}.show();
            } catch (Exception ignored) {}
        }
    }

    public static String getPcUsername() {
        try {
            String name = System.getProperty("user.name");
            return name == null || name.isBlank() ? "player" : name;
        } catch (Throwable t) {
            return "player";
        }
    }
}
