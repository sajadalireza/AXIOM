package com.axiom.app.presentation.ceremony;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class CeremonyEngine_Factory implements Factory<CeremonyEngine> {
  @Override
  public CeremonyEngine get() {
    return newInstance();
  }

  public static CeremonyEngine_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CeremonyEngine newInstance() {
    return new CeremonyEngine();
  }

  private static final class InstanceHolder {
    static final CeremonyEngine_Factory INSTANCE = new CeremonyEngine_Factory();
  }
}
