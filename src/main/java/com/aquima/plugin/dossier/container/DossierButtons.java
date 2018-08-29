package com.aquima.plugin.dossier.container;

import com.aquima.interactions.composer.model.Button;

public class DossierButtons {

  private final Button selectButton;
  private final Button deleteButton;

  public DossierButtons(Button selectButton, Button deleteButton) {
    this.selectButton = selectButton;
    this.deleteButton = deleteButton;
  }

  public Button getSelectButton() {
    return this.selectButton;
  }

  public Button getDeleteButton() {
    return this.deleteButton;
  }

  public boolean hasDeleteButton() {
    return this.deleteButton != null;
  }
}
