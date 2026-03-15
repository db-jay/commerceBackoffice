package com.commerce.backoffice.support.template;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/*
 * [역할]
 * - Application Service 테스트에서 반복되는 기본 설정을 제공한다.
 *
 * [왜 필요한가]
 * - UseCase 테스트는 보통 Spring 전체를 띄우지 않고,
 *   Port만 Mock으로 바꿔 흐름을 빠르게 확인한다.
 * - 이 설정을 매번 적으면 테스트 파일이 불필요하게 길어진다.
 *
 * [사용 방법]
 * - 이 클래스를 상속한다.
 * - @Mock으로 Port를 만들고, @InjectMocks로 Service를 주입한다.
 *
 * [주의할 점]
 * - 이 템플릿은 "Application 계층 전용"이다.
 * - DB, MockMvc, Testcontainers가 필요한 테스트에는 맞지 않는다.
 */
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(ReplaceUnderscores.class)
public abstract class ApplicationServiceTestTemplate {
}
