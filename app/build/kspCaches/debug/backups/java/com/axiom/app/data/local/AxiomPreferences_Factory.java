package com.axiom.app.data.local;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AxiomPreferences_Factory implements Factory<AxiomPreferences> {
  private final Provider<Context> contextProvider;

  private AxiomPreferences_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AxiomPreferences get() {
    return newInstance(contextProvider.get());
  }

  public static AxiomPreferences_Factory create(Provider<Context> contextProvider) {
    return new AxiomPreferences_Factory(contextProvider);
  }

  public static AxiomPreferences newInstance(Context context) {
    return new AxiomPreferences(context);
  }
}
