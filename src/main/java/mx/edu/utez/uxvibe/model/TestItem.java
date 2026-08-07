package mx.edu.utez.uxvibe.model;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TestItem {

  private static final DateTimeFormatter DATE_FORMATTER =
    DateTimeFormatter.ofPattern("dd/MM/yyyy");
  private static final Clock CLOCK = Clock.system(
    ZoneId.of("America/Mexico_City")
  );

  private String name;
  private String description;
  private String systemLink;
  private LocalDate createdOn;

  public TestItem() {
    this.createdOn = LocalDate.now(CLOCK);
  }

  public TestItem(String name) {
    this(name, LocalDate.now(CLOCK));
  }

  public TestItem(String name, LocalDate createdOn) {
    this(name, null, null, createdOn);
  }

  public TestItem(
    String name,
    String description,
    String systemLink,
    LocalDate createdOn
  ) {
    this.name = name;
    this.description = description;
    this.systemLink = systemLink;
    this.createdOn = createdOn == null ? LocalDate.now(CLOCK) : createdOn;
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
    this.createdOn = createdOn == null ? LocalDate.now(CLOCK) : createdOn;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getSystemLink() {
    return systemLink;
  }

  public void setSystemLink(String systemLink) {
    this.systemLink = systemLink;
  }

  public String getCreatedOnFormatted() {
    return createdOn.format(DATE_FORMATTER);
  }
}
