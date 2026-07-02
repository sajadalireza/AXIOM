package com.axiom.app;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class AwakenApplication_MembersInjector implements MembersInjector<AwakenApplication> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  private AwakenApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  @Override
  public void injectMembers(AwakenApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  public static MembersInjector<AwakenApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new AwakenApplication_MembersInjector(workerFactoryProvider);
  }

  @InjectedFieldSignature("com.axiom.app.AwakenApplication.workerFactory")
  public static void injectWorkerFactory(AwakenApplication instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
