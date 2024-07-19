package no.jonathan.quizapplication.quiz;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import net.datafaker.Faker;
import no.jonathan.quizapplication.exception.NotSupportedFileException;
import no.jonathan.quizapplication.exception.QuizNotFoundException;
import no.jonathan.quizapplication.shared.QuizDtoMapper;
import no.jonathan.quizapplication.user.User;
import no.jonathan.quizapplication.utils.AccessUtil;
import no.jonathan.quizapplication.utils.QuizUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

  private static final Faker FAKER = new Faker();

  @InjectMocks private QuizService underTest;

  @Mock private QuizRepository quizRepository;

  @Mock private QuizRepositoryCustom quizRepositoryCustom;

  @Mock private QuizUtil quizUtil;

  @Mock private QuizDtoMapper quizDtoMapper;

  @AfterEach
  void tearDown() {}

  @Test
  @DisplayName("It should get one quizzes created by user")
  void itShouldGetQuizzesByUser_1() {
    // Given
    String email = FAKER.internet().emailAddress();
    String title = FAKER.book().title();
    Pageable pageable = PageRequest.of(0, 10);

    QuizCoreProjection mockQuizCoreProjection = mock(QuizCoreProjection.class);
    Page<QuizCoreProjection> pageResult =
        new PageImpl<>(Collections.singletonList(mockQuizCoreProjection));

    // When
    when(quizRepositoryCustom.findAll(title, email, pageable)).thenReturn(pageResult);

    // Then
    var actual = underTest.getQuizzesByUser(title, email, pageable);

    assertEquals(1, actual.getContent().size());
  }

  @Test
  @DisplayName("It should get zero quizzes created by user")
  void itShouldGetQuizzesByUser_0() {
    // Given
    String email = FAKER.internet().emailAddress();
    String title = FAKER.book().title();
    Pageable pageable = PageRequest.of(0, 10);

    Page<QuizCoreProjection> pageResult = new PageImpl<>(Collections.emptyList());

    // When
    when(quizRepositoryCustom.findAll(title, email, pageable)).thenReturn(pageResult);

    // Then
    var actual = underTest.getQuizzesByUser(title, email, pageable);

    assertEquals(0, actual.getContent().size());
  }

  @Test
  @DisplayName("It should get quiz by id")
  void itShouldGetQuizById() {
    // Given
    Long quizId = FAKER.random().nextLong(0, 100);
    String entityCreatedByEmail = FAKER.internet().emailAddress();

    QuizDto mockQuizDto = mock(QuizDto.class);
    Authentication mockAuthentication = mock(Authentication.class);

    // When
    when(quizRepositoryCustom.findDtoById(quizId)).thenReturn(Optional.of(mockQuizDto));

    when(mockQuizDto.createdByEmail()).thenReturn(entityCreatedByEmail);

    when(mockAuthentication.getName()).thenReturn(entityCreatedByEmail);

    // Then
    var result = underTest.getQuizById(quizId, mockAuthentication);

    assertNotNull(result);
  }

  @Test
  @DisplayName("It will throw when getting a quiz by id that does not exist")
  void itWillThrowWhenGetQuizByIdNotFound() {
    // Given
    Long quizId = FAKER.random().nextLong(0, 100);

    Authentication mockAuthentication = mock(Authentication.class);

    // When
    when(quizRepositoryCustom.findDtoById(quizId)).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(() -> underTest.getQuizById(quizId, mockAuthentication))
        .isInstanceOf(QuizNotFoundException.class)
        .hasMessage("Quiz with id '" + quizId + "' was not found");
  }

  @Test
  @DisplayName("It should get a quiz by link (uuid)")
  void itShouldGetQuizByLink() {
    // Given
    UUID quizLink = UUID.randomUUID();
    String entityCreatedByEmail = FAKER.internet().emailAddress();

    QuizDto mockQuizDto = mock(QuizDto.class);
    Authentication mockAuthentication = mock(Authentication.class);

    // When
    when(quizRepositoryCustom.findDtoByLink(quizLink)).thenReturn(Optional.of(mockQuizDto));

    when(mockQuizDto.shared()).thenReturn(false);

    when(mockQuizDto.createdByEmail()).thenReturn(entityCreatedByEmail);

    when(mockAuthentication.getName()).thenReturn(entityCreatedByEmail);

    // Then
    var result = underTest.getQuizByLink(quizLink, mockAuthentication);

    assertNotNull(result);
  }

  @Test
  @DisplayName("It will throw when getting a quiz by link (uuid) that does not exist")
  void itWillThrowWhenGetQuizByLinkNotFound() {
    // Given
    UUID quizLink = UUID.randomUUID();

    Authentication mockAuthentication = mock(Authentication.class);

    // When
    when(quizRepositoryCustom.findDtoByLink(quizLink)).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(() -> underTest.getQuizByLink(quizLink, mockAuthentication))
        .isInstanceOf(QuizNotFoundException.class)
        .hasMessage("Quiz with link '" + quizLink + "' was not found");
  }

  @Test
  @DisplayName("It should not check for access if quiz is shared")
  void itShouldNotCheckAccessIfQuizIsShared() {
    // Given
    UUID quizLink = UUID.randomUUID();

    try (MockedStatic<AccessUtil> mockAccessUtil = mockStatic(AccessUtil.class)) {
      QuizDto mockQuizDto = mock(QuizDto.class);
      Authentication mockAuthentication = mock(Authentication.class);

      // When
      when(quizRepositoryCustom.findDtoByLink(quizLink)).thenReturn(Optional.of(mockQuizDto));

      when(mockQuizDto.shared()).thenReturn(true);

      // Then
      var result = underTest.getQuizByLink(quizLink, mockAuthentication);

      assertNotNull(result);
      verify(mockAuthentication, never()).getName();
      mockAccessUtil.verifyNoInteractions();
    }
  }

  @Test
  @DisplayName("It should check for access if quiz is not shared")
  void itShouldCheckAccessIfQuizNotShared() {
    // Given
    UUID quizLink = UUID.randomUUID();
    String entityCreatedByEmail = FAKER.internet().emailAddress();

    try (MockedStatic<AccessUtil> mockAccessUtil = mockStatic(AccessUtil.class)) {
      QuizDto mockQuizDto = mock(QuizDto.class);
      Authentication mockAuthentication = mock(Authentication.class);

      // When
      when(quizRepositoryCustom.findDtoByLink(quizLink)).thenReturn(Optional.of(mockQuizDto));

      when(mockQuizDto.shared()).thenReturn(false);

      when(mockQuizDto.createdByEmail()).thenReturn(entityCreatedByEmail);

      // Then
      var result = underTest.getQuizByLink(quizLink, mockAuthentication);

      assertNotNull(result);
      mockAccessUtil.verify(
          () -> AccessUtil.verifyUserHasAccessToEntity(entityCreatedByEmail, mockAuthentication),
          times(1));
    }
  }

  @Test
  @DisplayName("It should build a new quiz with ai")
  void itShouldBuildNewQuizWithAI() {
    try (MockedStatic<Quiz> mockQuizStatic = mockStatic(Quiz.class)) {
      // Given
      Authentication mockAuthentication = mock(Authentication.class);
      User mockUser = mock(User.class);

      String fileName = "animals_info.txt";
      String language = "french";
      byte[] bytes = {};

      Quiz mockQuiz = mock(Quiz.class);

      // When
      when(mockAuthentication.getPrincipal()).thenReturn(mockUser);

      when(quizRepository.save(any())).thenReturn(mockQuiz);

      // Then
      var result = underTest.buildNewQuizWithAI(mockAuthentication, fileName, language, bytes);

      assertNotNull(result);
      mockQuizStatic.verify(() -> Quiz.createUnfinishedQuiz(any()), times(1));
      verify(quizUtil, times(1)).continueAndFinishQuizCreation(any(), any(), any(), any());
    }
  }

  @Test
  @DisplayName("It will throw when file name is not supported when building quiz with ai")
  void itWillThrowWhenFileNotSupportedWhenBuildNewQuizWithAI() {
    // Given
    try (MockedStatic<Quiz> mockQuizStatic = mockStatic(Quiz.class)) {
      Authentication mockAuthentication = mock(Authentication.class);

      String fileName = "animals_info.pdf";
      String language = "french";
      byte[] bytes = {};

      // When
      // Then
      assertThatThrownBy(
              () -> underTest.buildNewQuizWithAI(mockAuthentication, fileName, language, bytes))
          .isInstanceOf(NotSupportedFileException.class)
          .hasMessage("Only .txt files are supported");

      mockQuizStatic.verify(() -> Quiz.createUnfinishedQuiz(any()), never());
      verify(quizUtil, never()).continueAndFinishQuizCreation(any(), any(), any(), any());
    }
  }

  @Test
  @DisplayName("It should delete a quiz by id")
  void itShouldDeleteQuiz() {
    // Given
    Long quizId = FAKER.random().nextLong(0, 100);
    Quiz mockQuiz = mock(Quiz.class);
    Authentication mockAuthentication = mock(Authentication.class);
    String entityCreatedByEmail = FAKER.internet().emailAddress();

    // When
    when(quizRepositoryCustom.findById(quizId)).thenReturn(Optional.of(mockQuiz));

    when(mockQuiz.getCreatedByEmail()).thenReturn(entityCreatedByEmail);

    when(mockAuthentication.getName()).thenReturn(entityCreatedByEmail);

    when(mockQuiz.getId()).thenReturn(quizId);

    // Then
    var result = underTest.deleteQuiz(quizId, mockAuthentication);

    assertEquals(quizId, result);
    verify(quizRepositoryCustom, times(1)).delete(mockQuiz);
  }

  @Test
  @DisplayName("It will throw when trying to delete a quiz by id")
  void itWillThrowWhenQuizNotFoundWhenDeleteQuiz() {
    // Given
    Long quizId = FAKER.random().nextLong(0, 100);
    Authentication mockAuthentication = mock(Authentication.class);

    // When
    when(quizRepositoryCustom.findById(quizId)).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(() -> underTest.deleteQuiz(quizId, mockAuthentication))
        .isInstanceOf(QuizNotFoundException.class)
        .hasMessage("Quiz with id '" + quizId + "' was not found");

    verify(quizRepository, never()).delete(any());
  }

  @Test
  @DisplayName("It should update quiz")
  void itShouldUpdateQuiz() {
    // Given
    Long quizId = FAKER.random().nextLong(0, 100);
    Quiz mockQuiz = mock(Quiz.class);
    QuizDto mockQuizDto = mock(QuizDto.class);
    QuizUpdateRequest updateRequest =
        new QuizUpdateRequest("new title", "new description", FAKER.random().nextBoolean());

    Authentication mockAuthentication = mock(Authentication.class);

    String entityCreatedByEmail = FAKER.internet().emailAddress();

    // When
    when(quizRepositoryCustom.findById(quizId)).thenReturn(Optional.of(mockQuiz));

    when(mockQuiz.getCreatedByEmail()).thenReturn(entityCreatedByEmail);

    when(mockAuthentication.getName()).thenReturn(entityCreatedByEmail);

    when(quizRepository.save(mockQuiz)).thenReturn(mockQuiz);
    when(quizDtoMapper.apply(mockQuiz)).thenReturn(mockQuizDto);

    // Then
    var result = underTest.updateQuiz(quizId, updateRequest, mockAuthentication);

    assertNotNull(result);
    verify(quizRepository, times(1)).save(mockQuiz);
    verify(quizDtoMapper, times(1)).apply(mockQuiz);
  }

  @Test
  @DisplayName("It will throw when quiz not found when updating quiz")
  void itWillThrowWhenQuizNotFoundWhenUpdateQuiz() {
    // Given
    Long quizId = FAKER.random().nextLong(0, 100);
    Quiz mockQuiz = mock(Quiz.class);
    QuizUpdateRequest updateRequest =
        new QuizUpdateRequest("new title", "new description", FAKER.random().nextBoolean());

    Authentication mockAuthentication = mock(Authentication.class);

    // When
    when(quizRepositoryCustom.findById(quizId)).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(() -> underTest.updateQuiz(quizId, updateRequest, mockAuthentication))
        .isInstanceOf(QuizNotFoundException.class)
        .hasMessage("Quiz with id '" + quizId + "' was not found");

    verify(quizRepository, never()).save(mockQuiz);
    verify(quizDtoMapper, never()).apply(mockQuiz);
  }
}
