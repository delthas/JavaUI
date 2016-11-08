package fr.delthas.uitest;

public enum Font {

  COMIC("comicsans");

  private final String name;

  private Font(String name) {
    this.name = name;
  }

  String getName() {
    return name;
  }

}
