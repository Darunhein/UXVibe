package mx.edu.utez.uxvibe.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TestItem {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String name;
    private LocalDate createdOn;

    public TestItem() {
        this.createdOn = LocalDate.now();
    }

    public TestItem(String name) {
        this(name, LocalDate.now());
    }

    public TestItem(String name, LocalDate createdOn) {
        this.name = name;
        this.createdOn = createdOn == null ? LocalDate.now() : createdOn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn == null ? LocalDate.now() : createdOn;
    }

    public String getCreatedOnFormatted() {
        return createdOn.format(DATE_FORMATTER);
    }
}
