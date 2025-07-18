package me.alexliudev.ycithelper;

import me.shedaniel.autoconfig.AutoConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

public class LogFilter implements Filter {
    @Override
    public Result getOnMismatch() {
        return Result.NEUTRAL;
    }

    @Override
    public Result getOnMatch() {
        return Result.NEUTRAL;
    }

    public Result checkMessage(String message) {
        if (!AutoConfig.getConfigHolder(ModConfig.class).getConfig().isEnableLogFilter()) return Result.NEUTRAL;
        if (message.startsWith("Received passengers for unknown entity")) return Result.DENY;
        if (message.startsWith("Unable to play empty soundEvent:")) return Result.DENY;
        if (message.startsWith("Received packet for unknown objective")) return Result.DENY;
        return Result.NEUTRAL;
    }

    public State getState() {
        try {
            return State.STARTED;
        } catch (Exception var2) {
            return null;
        }
    }

    public void initialize() {
    }

    public boolean isStarted() {
        return true;
    }

    public boolean isStopped() {
        return false;
    }

    public void start() {
    }

    public void stop() {
    }

    public Result filter(LogEvent event) {
        return this.checkMessage(event.getMessage().getFormattedMessage());
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object... arg4) {
        return this.checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4) {
        return this.checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, Object message, Throwable arg4) {
        return this.checkMessage(message.toString());
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, Message message, Throwable arg4) {
        return this.checkMessage(message.getFormattedMessage());
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5) {
        return this.checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6) {
        return this.checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7) {
        return this.checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8) {
        return this.checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9) {
        return this.checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10) {
        return this.checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11) {
        return this.checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11, Object arg12) {
        return this.checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13) {
        return this.checkMessage(message);
    }
}
