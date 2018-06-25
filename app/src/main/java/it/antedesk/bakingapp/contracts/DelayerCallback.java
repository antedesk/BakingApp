package it.antedesk.bakingapp.contracts;

import java.util.List;

import it.antedesk.bakingapp.model.Recipe;

/**
 * Created by Antedesk on 22/06/2018.
 */

public interface DelayerCallback {
    void onDone(List<Recipe> recipes);
}
