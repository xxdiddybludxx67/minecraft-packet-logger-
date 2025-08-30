package packetlogger;

import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.ChatComponentText;

import java.awt.Desktop;
import java.io.File;
import java.lang.reflect.Field;

public class ChatCommandHandler {

    @SubscribeEvent
    public void onChatInput(Post event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.currentScreen instanceof GuiChat) {
            GuiChat chat = (GuiChat) mc.currentScreen;
            try {
                Field field = GuiChat.class.getDeclaredField("inputField");
                field.setAccessible(true);
                Object guiTextField = field.get(chat);

                String text = (String) guiTextField.getClass().getMethod("getText").invoke(guiTextField);

                // &help command
                if (text.equalsIgnoreCase("&help")) {
                    guiTextField.getClass().getMethod("setText", String.class).invoke(guiTextField, "");
                    mc.thePlayer.addChatMessage(new ChatComponentText("§a[PacketLogger] Available Commands:"));
                    mc.thePlayer.addChatMessage(new ChatComponentText("§e&toggle §7- Enable/Disable packet logging"));
                    mc.thePlayer.addChatMessage(new ChatComponentText("§e&status §7- Show current logging status"));
                    mc.thePlayer.addChatMessage(new ChatComponentText("§e&packetfolder §7- Open logs folder"));
                    return;
                }

                // &toggle command
                if (text.equalsIgnoreCase("&toggle")) {
                    guiTextField.getClass().getMethod("setText", String.class).invoke(guiTextField, "");

                    // Flip state
                    PacketLoggerMod.interceptor.setLoggingEnabled(!PacketLoggerMod.interceptor.isLoggingEnabled());
                    boolean enabled = PacketLoggerMod.interceptor.isLoggingEnabled(); // get updated state

                    mc.thePlayer.addChatMessage(new ChatComponentText("§a[PacketLogger] Logging " + (enabled ? "ENABLED" : "DISABLED")));
                    return;
                }

                // &status command
                if (text.equalsIgnoreCase("&status")) {
                    guiTextField.getClass().getMethod("setText", String.class).invoke(guiTextField, "");
                    boolean enabled = PacketLoggerMod.interceptor.isLoggingEnabled();
                    mc.thePlayer.addChatMessage(new ChatComponentText("§a[PacketLogger] Status: " + (enabled ? "ENABLED" : "DISABLED")));
                    mc.thePlayer.addChatMessage(new ChatComponentText("§7Current log file: " + PacketLoggerMod.logWriter.getCurrentPath()));
                    return;
                }

                // &packetfolder command
                if (text.equalsIgnoreCase("&packetfolder")) {
                    guiTextField.getClass().getMethod("setText", String.class).invoke(guiTextField, "");

                    File folder = PacketLoggerMod.logFolder;
                    mc.thePlayer.addChatMessage(new ChatComponentText("§a[PacketLogger] Logs folder: " + folder.getAbsolutePath()));

                    try {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(folder);
                        }
                    } catch (Exception ex) {
                        mc.thePlayer.addChatMessage(new ChatComponentText("§c[PacketLogger] Failed to open folder."));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
