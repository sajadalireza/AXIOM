package com.axiom.app.presentation.firstwin

import com.axiom.app.domain.firstwin.FirstWinArea
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FirstWinDraftStateTest {
    @Test fun fresh_hasNoPreselection_andContinueDisabled() {
        val state = FirstWinDraftReducer.fresh()
        assertEquals(FirstWinDraftStep.AREA, state.step)
        assertEquals(null, state.selectedArea)
        assertFalse(state.canContinueArea)
    }

    @Test fun selectArea_isSingleSelection_andEnablesContinue() {
        val first = FirstWinDraftReducer.selectArea(FirstWinDraftReducer.fresh(), FirstWinArea.WORK)
        val second = FirstWinDraftReducer.selectArea(first, FirstWinArea.HEALTH)
        assertEquals(FirstWinArea.HEALTH, second.selectedArea)
        assertTrue(second.canContinueArea)
    }

    @Test fun continue_requiresArea_andMovesToAction() {
        val fresh = FirstWinDraftReducer.fresh()
        assertEquals(FirstWinDraftStep.AREA, FirstWinDraftReducer.continueFromArea(fresh).step)
        val selected = FirstWinDraftReducer.selectArea(fresh, FirstWinArea.STUDY)
        assertEquals(FirstWinDraftStep.ACTION, FirstWinDraftReducer.continueFromArea(selected).step)
    }

    @Test fun actionTitle_isDraftOnly_andRequiresThreeCharacters() {
        val selected = FirstWinDraftReducer.selectArea(FirstWinDraftReducer.fresh(), FirstWinArea.PERSONAL)
        val action = FirstWinDraftReducer.continueFromArea(selected)
        val tooShort = FirstWinDraftReducer.setActionTitle(action, " x ")
        val valid = FirstWinDraftReducer.setActionTitle(action, "  Read one page  ")
        assertFalse(tooShort.canCreateMission)
        assertTrue(valid.canCreateMission)
        assertEquals("  Read one page  ", valid.actionTitle)
    }

    @Test fun backToArea_preservesArea_butClearsActionDraft() {
        val state = FirstWinDraftReducer.setActionTitle(
            FirstWinDraftReducer.continueFromArea(
                FirstWinDraftReducer.selectArea(FirstWinDraftReducer.fresh(), FirstWinArea.WORK)
            ),
            "Send one email",
        )
        val back = FirstWinDraftReducer.backToArea(state)
        assertEquals(FirstWinDraftStep.AREA, back.step)
        assertEquals(FirstWinArea.WORK, back.selectedArea)
        assertEquals("", back.actionTitle)
    }
}
