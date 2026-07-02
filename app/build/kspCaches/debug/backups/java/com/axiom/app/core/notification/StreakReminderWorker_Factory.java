package com.axiom.app.core.notification;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.axiom.app.data.local.AxiomPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class StreakReminderWorker_Factory {
  private final Provider<AxiomPreferences> preferencesProvider;

  private StreakReminderWorker_Factory(Provider<AxiomPreferences> preferencesProvider) {
    this.preferencesProvider = preferencesProvider;
  }

  public StreakReminderWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, preferencesProvider.get());
  }

  public static StreakReminderWorker_Factory create(
      Provider<AxiomPreferences> preferencesProvider) {
    return new StreakReminderWorker_Factory(preferencesProvider);
  }

  public static StreakReminderWorker newInstance(Context context, WorkerParameters params,
      AxiomPreferences preferences) {
    return new StreakReminderWorker(context, params, preferences);
  }
}
