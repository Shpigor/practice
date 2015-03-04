#include <stdio.h>
#include "stm32f4xx.h"
#include "stm32f4xx_hal_gpio.h"
#include "stm32f4xx_hal_usart.h"
#include "cmsis_device.h"

#define RED_LED			GPIO_PIN_14
#define GREEN_LED		GPIO_PIN_13
#define USER_BUTTON		GPIO_PIN_0

#define GREEN_ON() 		HAL_GPIO_WritePin(GPIOG, GREEN_LED, GPIO_PIN_SET)
#define GREEN_OFF()  	HAL_GPIO_WritePin(GPIOG, GREEN_LED, GPIO_PIN_RESET)

#define RED_ON()  		HAL_GPIO_WritePin(GPIOG, RED_LED, GPIO_PIN_SET)
#define RED_OFF()		HAL_GPIO_WritePin(GPIOG, RED_LED, GPIO_PIN_RESET)

#define DELAY			1000

// Button defines
#define READ_BUTTON()	HAL_GPIO_ReadPin(GPIOA, USER_BUTTON)
#define PRESED			GPIO_PIN_SET
#define UNPRESED		GPIO_PIN_RESET

volatile uint32_t delay;

// Button handler
volatile uint8_t state = 0;
volatile uint32_t dynamic_delay = 0;
uint32_t delay_list[] = { 100, 500, DELAY, 2000 };
uint8_t delay_index = 0;

void check_button_state(void);

void SysTick_Handler() {
	if (delay > 0) {
		delay--;
	}
	check_button_state();
}

void check_button_state() {
	if (READ_BUTTON() == PRESED) {
		if (state == UNPRESED) {
			state = PRESED;
			dynamic_delay = delay_list[delay_index];
			if (delay_index == 3) {
				delay_index = 0;
			} else {
				delay_index++;
			}
		}
	} else {
		if (state == PRESED) {
			state = UNPRESED;
		}
	}
}

void sleep_ms(uint32_t sleep_ms) {
	delay = sleep_ms;
	while (delay)
		;
}

void leds_init() {
	RCC->AHB1ENR |= RCC_AHB1ENR_GPIOGEN; // enable G register

	GPIO_InitTypeDef GPIO_InitStruct;
	GPIO_InitStruct.Speed = GPIO_SPEED_HIGH;
	GPIO_InitStruct.Mode = GPIO_MODE_OUTPUT_PP;
	GPIO_InitStruct.Pull = GPIO_NOPULL;
	GPIO_InitStruct.Pin = GREEN_LED | RED_LED;

	HAL_GPIO_Init(GPIOG, &GPIO_InitStruct);
}

void user_button_init() {
	RCC->AHB1ENR |= RCC_AHB1ENR_GPIOAEN; // enable A register

	GPIO_InitTypeDef GPIO_InitStructButton;
	GPIO_InitStructButton.Speed = GPIO_SPEED_MEDIUM;
	GPIO_InitStructButton.Mode = GPIO_MODE_INPUT;
	GPIO_InitStructButton.Pull = GPIO_NOPULL;
	GPIO_InitStructButton.Pin = USER_BUTTON;

	HAL_GPIO_Init(GPIOA, &GPIO_InitStructButton);
}

void usart_init() {
	RCC->AHB1ENR |= RCC_AHB1ENR_GPIOAEN; // enable A register

	GPIO_InitTypeDef GPIO_InitStructUSART2;
	GPIO_InitStructUSART2.Speed = GPIO_SPEED_HIGH;
	GPIO_InitStructUSART2.Mode = GPIO_MODE_AF_PP;
	GPIO_InitStructUSART2.Pull = GPIO_PULLUP;
	GPIO_InitStructUSART2.Pin = GPIO_PIN_2 | GPIO_PIN_3;

	HAL_GPIO_Init(GPIOA, &GPIO_InitStructUSART2);
}

int get_delay() {
	if (dynamic_delay == 0) {
		dynamic_delay = DELAY;
	}
	return dynamic_delay;
}

int main() {
	SysTick_Config(SystemCoreClock / 1000); //1 ms
	leds_init();
	user_button_init();
	while (1) {
		GREEN_ON();
		RED_OFF();
		sleep_ms(get_delay());
		RED_ON();
		GREEN_OFF();
		sleep_ms(get_delay());
	}
}
