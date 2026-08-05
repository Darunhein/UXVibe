package mx.edu.utez.uxvibe.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ParticipantItem {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("d/MM/yyyy, h:mm a", new Locale("es", "MX"));

    private String name;
    private String description;
    private int durationMinutes;
    private LocalDateTime completedOn;

    public ParticipantItem() {
        this.completedOn = LocalDateTime.now();
    }

    public ParticipantItem(String name, String description, int durationMinutes, LocalDateTime completedOn) {
        this.name = name;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.completedOn = completedOn == null ? LocalDateTime.now() : completedOn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public LocalDateTime getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(LocalDateTime completedOn) {
        this.completedOn = completedOn == null ? LocalDateTime.now() : completedOn;
    }

    public String getDurationLabel() {
        return durationMinutes + " min";
    }

    public String getCompletedOnFormatted() {
        return completedOn.format(DATE_TIME_FORMATTER)
                .replace("AM", "a.m.")
                .replace("PM", "p.m.");
    }
}
