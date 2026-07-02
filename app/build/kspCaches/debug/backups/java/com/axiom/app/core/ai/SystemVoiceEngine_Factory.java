package com.axiom.app.core.ai;

import com.axiom.app.data.local.AxiomPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class SystemVoiceEngine_Factory implements Factory<SystemVoiceEngine> {
  private final Provider<AxiomPreferences> preferencesProvider;

  private SystemVoiceEngine_Factory(Provider<AxiomPreferences> preferencesProvider) {
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public SystemVoiceEngine get() {
    return newInstance(preferencesProvider.get());
  }

  public static SystemVoiceEngine_Factory create(Provider<AxiomPreferences> preferencesProvider) {
    return new SystemVoiceEngine_Factory(preferencesProvider);
  }

  public static SystemVoiceEngine newInstance(AxiomPreferences preferences) {
    return new SystemVoiceEngine(preferences);
  }
}
