package fr.delthas.javaui;

public enum Font {
  
  COMIC("comicsans");
  private final String name;
  
  Font(String name) {
    this.name = name;
  }
  
  String getName() {
    return name;
  }
  
}
