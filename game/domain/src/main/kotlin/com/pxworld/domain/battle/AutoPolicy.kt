package com.pxworld.domain.battle

object AutoPolicy {

    private val SLOT_PRIORITY = listOf(SkillSlot.ULTIMATE, SkillSlot.SKILL, SkillSlot.BASIC)

    fun choose(state: BattleState, legal: List<BattleCommand>): BattleCommand {
        require(legal.isNotEmpty()) { "no legal command to choose from" }
        val actor = state.combatant(legal.first().actor)
        val bySlot = legal.groupBy { actor.setup.skill(it.skillId).slot }
        val preferred = SLOT_PRIORITY.firstNotNullOf { bySlot[it] }
        val skill = actor.setup.skill(preferred.first().skillId)
        val sameSkill = preferred.filter { it.skillId == skill.id }
        return if (skill.targeting == Targeting.SingleAlly) {
            sameSkill.minWith(compareBy({ state.combatant(requireNotNull(it.target)).hpPermille }, { requireNotNull(it.target).slot }))
        } else {
            sameSkill.first()
        }
    }
}
