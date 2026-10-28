package com.example.my_rouge_mobile.ui.dialogs

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.my_rouge_mobile.mapping.SpriteMapping
import com.example.my_rouge_mobile.ui.components.FramedPanel
import com.example.my_rouge_mobile.ui.components.GameButton
import com.example.my_rouge_mobile.ui.theme.GameColors
import com.example.my_rouge_mobile.viewmodel.BackpackTab
import rougelite.BusinessLogicLayer.inventory.Backpack
import rougelite.BusinessLogicLayer.objects.Ammo
import rougelite.BusinessLogicLayer.objects.GameObject
import rougelite.BusinessLogicLayer.objects.HealthPotion
import rougelite.BusinessLogicLayer.objects.PortalStone
import rougelite.BusinessLogicLayer.objects.Reinforce
import rougelite.BusinessLogicLayer.objects.Weapon

/** Строка слота. Цвет берётся из SpriteMapping - тот же, что у предмета на карте. */
private data class SlotRow(val text: String, val color: Color, val equipped: Boolean = false)

private fun itemName(item: GameObject): String = when (item) {
    is PortalStone -> item.position.russianName
    is Weapon -> item.name
    is Ammo -> item.name
    is HealthPotion -> item.name
    is Reinforce -> item.name
    else -> item.javaClass.simpleName
}

private fun slotRows(b: Backpack, tab: BackpackTab): List<SlotRow> = when (tab) {
    BackpackTab.Weapon -> b.weapons.map { w ->
        SlotRow(w.name, SpriteMapping.weaponColor(w.type), equipped = w == b.equippedWeapon)
    }
    BackpackTab.Ammo -> b.ammo.map {
        SlotRow("${it.name}  (${it.amountAmmo} ед.)", SpriteMapping.ammoColor(it.type))
    }
    BackpackTab.Potion -> b.healthPotion.map {
        SlotRow("${it.name}  (+${it.healthPower} HP)", SpriteMapping.potionColor(it.type))
    }
    BackpackTab.Reinforce -> b.reinforces.map { r ->
        val color = (r as? Reinforce)?.let { SpriteMapping.reinforceColor(it) } ?: GameColors.Gold
        SlotRow(itemName(r), color)
    }
}

/** Рюкзак (бывший BackpackWindow). Вместо клавиш 1..9 - касание по слоту, вместо стрелок - касание по вкладке. */
@Composable
fun BackpackDialog(
    backpack: Backpack,
    tab: BackpackTab,
    onSelectTab: (BackpackTab) -> Unit,
    onUseItem: (slot: Int) -> Unit,
    onClose: () -> Unit
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        FramedPanel(
            title = "РЮКЗАК",
            modifier = Modifier.fillMaxWidth(0.94f).heightIn(max = 640.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                BackpackTab.entries.forEach { t ->
                    val active = t == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(if (active) 2.dp else 1.dp, if (active) GameColors.GoldBright else GameColors.Gold)
                            .clickable { onSelectTab(t) }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            t.title,
                            color = if (active) GameColors.GoldBright else GameColors.Gold,
                            fontSize = 10.sp,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            // подсказка по цветам усилений
            if (tab == BackpackTab.Reinforce) {
                Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("■ Здоровье", color = SpriteMapping.REINFORCE_HEALTH_COLOR, fontSize = 12.sp)
                    Text("■ Сила", color = SpriteMapping.REINFORCE_STRENGTH_COLOR, fontSize = 12.sp)
                    Text("■ Ловкость", color = SpriteMapping.REINFORCE_DEXTERITY_COLOR, fontSize = 12.sp)
                }
            }

            val rows = slotRows(backpack, tab)
            for (slot in 0 until 9) {
                val row = rows.getOrNull(slot)
                val lineColor = row?.color ?: GameColors.Gold
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                        .border(
                            width = if (row?.equipped == true) 2.dp else 1.dp,
                            color = if (row?.equipped == true) lineColor else lineColor.copy(alpha = 0.4f)
                        )
                        .clickable(enabled = row != null) { onUseItem(slot) }
                        .padding(12.dp)
                ) {
                    Text(
                        text = if (row == null) "${slot + 1}. (пусто)"
                        else "${slot + 1}. ${if (row.equipped) "» " else ""}${row.text}",
                        color = if (row == null) GameColors.Gold.copy(alpha = 0.5f) else row.color,
                        fontWeight = if (row?.equipped == true) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            GameButton("ЗАКРЫТЬ", onClose)
        }
    }
}