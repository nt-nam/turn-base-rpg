package com.game.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test cho TurnResult.
 * TurnResult là POJO thuần Java, không phụ thuộc LibGDX.
 */
class TurnResultTest {

    private TurnResult result;

    @BeforeEach
    void setUp() {
        result = new TurnResult();
    }

    @Test
    @DisplayName("Constructor mặc định phải khởi tạo giá trị zero/false/rỗng")
    void defaultConstructor_allFieldsAreDefault() {
        assertEquals("",    result.actorId);
        assertEquals("",    result.targetId);
        assertEquals("",    result.skillUsed);
        assertEquals(0,     result.damage);
        assertFalse(result.isCritical);
        assertFalse(result.isMiss);
        assertFalse(result.targetDead);
        assertNull(result.actorEntity);
        assertNull(result.targetEntity);
        assertNotNull(result.state);
        assertTrue(result.state.isEmpty());
        assertEquals("",    result.effectDescription);
    }

    @Test
    @DisplayName("reset() phải đặt lại tất cả field về giá trị mặc định")
    void reset_clearsAllFields() {
        // Set một số giá trị
        result.actorId   = "actor1";
        result.damage    = 100;
        result.isCritical = true;
        result.targetDead = true;
        result.state.put("armor", 5f);

        result.reset();

        assertEquals("", result.actorId);
        assertEquals(0, result.damage);
        assertFalse(result.isCritical);
        assertFalse(result.targetDead);
        assertTrue(result.state.isEmpty());
    }

    @Test
    @DisplayName("toString() phải chứa actorId và damage")
    void toString_containsKeyInfo() {
        result.actorId = "hero1";
        result.damage  = 42;
        String str = result.toString();
        assertTrue(str.contains("hero1"), "toString phải chứa actorId");
        assertTrue(str.contains("42"),    "toString phải chứa damage");
    }

    @Test
    @DisplayName("state map có thể set và get")
    void state_canPutAndGet() {
        result.state.put("armor", 10f);
        result.state.put("critChance", 0.2f);
        assertEquals(10f,  result.state.get("armor"),     0.001f);
        assertEquals(0.2f, result.state.get("critChance"), 0.001f);
    }
}
