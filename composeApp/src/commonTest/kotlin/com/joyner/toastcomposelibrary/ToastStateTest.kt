package com.joyner.toastcomposelibrary

import com.joyner.toastcomposelibrary.toast.ToastState
import com.joyner.toastcomposelibrary.toast.ToastType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ToastStateTest {

    // ── isVisible ──────────────────────────────────────────────────────────

    @Test
    fun isVisible_returnsFalse_whenNoToastShown() {
        val state = ToastState()
        assertFalse(state.isVisible())
    }

    @Test
    fun isVisible_returnsTrue_afterShow() {
        val state = ToastState()
        state.show(message = "Hello")
        assertTrue(state.isVisible())
    }

    // ── show ───────────────────────────────────────────────────────────────

    @Test
    fun show_setsCurrentToast_whenNothingVisible() {
        val state = ToastState()
        state.show(message = "Hello", type = ToastType.SUCCESS)
        assertEquals("Hello", state.currentToast.message)
        assertEquals(ToastType.SUCCESS, state.currentToast.type)
    }

    @Test
    fun show_queuesSecondToast_whenOneIsVisible() {
        val state = ToastState()
        state.show(message = "First")
        state.show(message = "Second")
        // Current toast is still the first one
        assertEquals("First", state.currentToast.message)
    }

    @Test
    fun show_dropsToast_whenQueueIsFull() {
        val state = ToastState(maxQueueSize = 1)
        state.show(message = "First") // shown immediately
        state.show(message = "Second") // queued (queue size = 1, at limit)
        state.show(message = "Third") // dropped — queue is full

        // Advance to second toast
        state.reset()
        assertEquals("Second", state.currentToast.message)

        // Advance again — queue should be empty now, not "Third"
        state.reset()
        assertFalse(state.isVisible())
    }

    // ── dismiss ────────────────────────────────────────────────────────────

    @Test
    fun dismiss_clearsMessage_makingToastInvisible() {
        val state = ToastState()
        state.show(message = "Hello")
        assertTrue(state.isVisible())

        state.dismiss()
        // dismiss() synchronously clears the message; the coroutine handles reset() later
        assertFalse(state.isVisible())
    }

    @Test
    fun dismiss_doesNothing_whenNoToastVisible() {
        val state = ToastState()
        // Should not throw
        state.dismiss()
        assertFalse(state.isVisible())
    }

    // ── reset ──────────────────────────────────────────────────────────────

    @Test
    fun reset_dequeuesNextToast_whenQueueNonEmpty() {
        val state = ToastState()
        state.show(message = "First")
        state.show(message = "Second")

        state.reset()
        assertEquals("Second", state.currentToast.message)
        assertTrue(state.isVisible())
    }

    @Test
    fun reset_clearsCurrentToast_whenQueueEmpty() {
        val state = ToastState()
        state.show(message = "Only one")

        state.reset()
        assertFalse(state.isVisible())
        assertEquals("", state.currentToast.message)
    }

    // ── dismissAll ─────────────────────────────────────────────────────────

    @Test
    fun dismissAll_clearsCurrentAndQueue() {
        val state = ToastState(maxQueueSize = 5)
        state.show(message = "First")
        state.show(message = "Second")
        state.show(message = "Third")

        state.dismissAll()

        // Current toast message blanked (exit animation in flight, but message gone)
        assertFalse(state.isVisible())
        // Queue drained — reset() should not restore anything
        state.reset()
        assertFalse(state.isVisible())
    }

    // ── defaults ───────────────────────────────────────────────────────────

    @Test
    fun show_usesDefaultType_whenNotSpecified() {
        val state = ToastState()
        state.show(message = "Info toast")
        assertEquals(ToastType.INFO, state.currentToast.type)
    }

    @Test
    fun show_onAction_isNull_byDefault() {
        val state = ToastState()
        state.show(message = "No action")
        assertNull(state.currentToast.onAction)
    }

    @Test
    fun show_storesOnAction_whenProvided() {
        val state = ToastState()
        var triggered = false
        state.show(message = "With action", onAction = { triggered = true })

        state.currentToast.onAction?.invoke()
        assertTrue(triggered)
    }

    // ── maxQueueSize ───────────────────────────────────────────────────────

    @Test
    fun maxQueueSize_defaultIsThree() {
        assertEquals(ToastState.DefaultMaxQueueSize, ToastState().maxQueueSize)
        assertEquals(3, ToastState.DefaultMaxQueueSize)
    }

    @Test
    fun maxQueueSize_canBeCustomised() {
        val state = ToastState(maxQueueSize = 10)
        assertEquals(10, state.maxQueueSize)
    }
}
