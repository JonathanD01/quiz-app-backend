package no.jonathan.quizapplication;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import java.util.function.Function;
import no.jonathan.quizapplication.shared.BaseEntity;
import no.jonathan.quizapplication.shared.QBaseEntity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class ModularityTests {

  DescribedPredicate<JavaClass> IGNORED_CLASSES =
      JavaClass.Predicates.resideInAPackage("no.jonathan.quizapplication.shared")
          .and(
              JavaClass.Predicates.belongToAnyOf(
                  Function.class, QBaseEntity.class, BaseEntity.class));
  ApplicationModules modules = ApplicationModules.of(QuizApiApplication.class, IGNORED_CLASSES);

  @Test
  @Disabled
  void verifiesModularStructure() {
    // Investigate IGNORED_CLASSES
    // modules.verify();
  }

  @Test
  void writeDocumentationSnippets() {

    var canvasOptions = Documenter.CanvasOptions.defaults();

    var docOptions =
        Documenter.DiagramOptions.defaults().withStyle(Documenter.DiagramOptions.DiagramStyle.UML);

    new Documenter(modules).writeDocumentation(docOptions, canvasOptions);
  }
}
