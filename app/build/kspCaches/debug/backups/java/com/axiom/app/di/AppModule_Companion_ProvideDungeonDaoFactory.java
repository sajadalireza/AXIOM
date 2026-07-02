package com.axiom.app.di;

import com.axiom.app.data.local.AxiomDatabase;
import com.axiom.app.data.local.dao.DungeonDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_Companion_ProvideDungeonDaoFactory implements Factory<DungeonDao> {
  private final Provider<AxiomDatabase> dbProvider;

  private AppModule_Companion_ProvideDungeonDaoFactory(Provider<AxiomDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public DungeonDao get() {
    return provideDungeonDao(dbProvider.get());
  }

  public static AppModule_Companion_ProvideDungeonDaoFactory create(
      Provider<AxiomDatabase> dbProvider) {
    return new AppModule_Companion_ProvideDungeonDaoFactory(dbProvider);
  }

  public static DungeonDao provideDungeonDao(AxiomDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.Companion.provideDungeonDao(db));
  }
}
