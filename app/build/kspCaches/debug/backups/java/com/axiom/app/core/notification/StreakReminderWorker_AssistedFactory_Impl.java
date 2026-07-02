package com.axiom.app.core.notification;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class StreakReminderWorker_AssistedFactory_Impl implements StreakReminderWorker_AssistedFactory {
  private final StreakReminderWorker_Factory delegateFactory;

  StreakReminderWorker_AssistedFactory_Impl(StreakReminderWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public StreakReminderWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<StreakReminderWorker_AssistedFactory> create(
      StreakReminderWorker_Factory delegateFactory) {
    return InstanceFactory.create(new StreakReminderWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<StreakReminderWorker_AssistedFactory> createFactoryProvider(
      StreakReminderWorker_Factory delegateFactory) {
    return InstanceFactory.create(new StreakReminderWorker_AssistedFactory_Impl(delegateFactory));
  }
}
