package pw.valaria.muhlogger;

import com.destroystokyo.paper.utils.PaperPluginLogger;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.PluginClassLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pw.valaria.muhlogger.util.LoggerUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class Muhlogger extends JavaPlugin {

    List<String> loggers;
    List<Logger> injected = new ArrayList<>();
    Formatter formatter = new SimpleFormatter();

    @Override
    public void onLoad() {
        saveDefaultConfig();
        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        doRemove();
        super.reloadConfig();

        loggers = this.getConfig().getStringList("loggers");
        doApply();
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return false;
        }

        if ("reload".equals(args[0].toLowerCase(Locale.ROOT))) {
            reloadConfig();
            sender.sendMessage(Component.text("Reloaded muh logger!", NamedTextColor.RED));
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 0 || args.length == 1) {
            return List.of("reload");
        } else return List.of();
    }

    private void doApply() {
        loggers.forEach((loggerName) -> {
            // Faux a PDF, the logic this calls literally doesn't care
            final Logger logger = PaperPluginLogger.getLogger(new PluginDescriptionFile(loggerName, "unknown", "unknown"));
            applyHandler(logger);
        });
    }

    private void applyHandler(Logger logger) {

        final Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
            if (handler.getClass().getClassLoader() instanceof PluginClassLoader classLoader) {
                if (classLoader.getPlugin().getName().equals(this.getName())) {
                    getLogger().removeHandler(handler);
                }
            }
        }

        boolean hasComponentLogger;
        try {
            Plugin.class.getDeclaredMethod("getComponentLogger");
            hasComponentLogger = true;
        } catch (NoSuchMethodException e) {
            hasComponentLogger = false;
        }

        if (hasComponentLogger) {
            logger.addHandler(new Handler() {
                @Override
                public void publish(LogRecord record) {
                    ComponentLogger componentLogger = ComponentLogger.logger(record.getLoggerName());
                    Component output = LegacyComponentSerializer.legacySection().deserialize(formatter.formatMessage(record));

                    org.slf4j.event.Level level = LoggerUtil.getLevel(record.getLevel());
                    switch (level) {
                        case ERROR:
                            componentLogger.error(output, record.getThrown());
                            break;
                        case WARN:
                            componentLogger.warn(output, record.getThrown());
                            break;
                        case INFO:
                            componentLogger.info(output, record.getThrown());
                            break;
                        case DEBUG:
                            componentLogger.debug(output, record.getThrown());
                            break;
                        case TRACE:
                            componentLogger.trace(output, record.getThrown());
                            break;
                        default:
                            componentLogger.info(output, record.getThrown());
                            break;
                    }

                }

                @Override
                public void flush() {
                }

                @Override
                public void close() throws SecurityException {
                }
            });

        } else {
            logger.addHandler(new Handler() {
                                  @Override
                                  public void publish(LogRecord record) {
                                      final TextComponent deserialize = LegacyComponentSerializer.legacySection().deserialize(formatter.formatMessage(record));
                                      Component prefix;
                                      if (record.getLoggerName().equals("Minecraft")) {
                                          prefix = Component.text().asComponent();
                                      } else {
                                          prefix = Component.text("[" + record.getLoggerName() + "] ");
                                      }
                                      Bukkit.getConsoleSender().sendMessage(prefix.append(deserialize));
                                      if (record.getThrown() != null) {
                                          record.getThrown().printStackTrace();
                                      }
                                  }

                                  @Override
                                  public void flush() {

                                  }

                                  @Override
                                  public void close() throws SecurityException {

                                  }
                              }
            );
        }
        logger.setUseParentHandlers(false);
        injected.add(logger);
    }

    private void doRemove() {
        final Iterator<Logger> iterator = injected.iterator();

        while (iterator.hasNext()) {
            final Logger logger = iterator.next();
            final Handler[] handlers = logger.getHandlers();
            for (Handler handler : handlers) {
                if (handler.getClass().getClassLoader() instanceof PluginClassLoader classLoader) {
                    if (classLoader.getPlugin().getName().equals(this.getName())) {
                        logger.removeHandler(handler);
                    }
                }
            }
            logger.setUseParentHandlers(true);
            iterator.remove();
        }
    }
}
