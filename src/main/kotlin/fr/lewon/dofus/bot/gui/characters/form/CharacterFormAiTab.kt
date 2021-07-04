package fr.lewon.dofus.bot.gui.characters.form

import fr.lewon.dofus.bot.gui.characters.form.ai.BuffSpellCombination
import fr.lewon.dofus.bot.gui.characters.form.ai.RangeSpellCombination
import fr.lewon.dofus.bot.gui.custom.IntegerJTextField
import fr.lewon.dofus.bot.gui.custom.OutlineJLabel
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import java.awt.Color
import java.awt.Font
import javax.swing.JPanel
import javax.swing.JTextField

class CharacterFormAiTab(
    private val characterFormFrame: CharacterFormFrame,
    w: Int,
    h: Int,
    character: DofusCharacter = DofusCharacter()
) : JPanel() {

    private val mpBuffLabel = OutlineJLabel("MP buffs")
    private val losAttacksLabel = OutlineJLabel("LOS attacks")
    private val nonLosAttacksLabel = OutlineJLabel("Non LOS attacks")
    private val gapCloserLabel = OutlineJLabel("Gap closer")

    private val losAttacksKeysLabel = OutlineJLabel("Keys")
    private val losAttacksMinRangeLabel = OutlineJLabel("Min range")
    private val losAttacksMaxRangeLabel = OutlineJLabel("Max range")
    private val losAttacksKeysTextField = JTextField()
    private val losAttacksMinRangeTextField = IntegerJTextField()
    private val losAttacksMaxRangeTextField = IntegerJTextField()

    private val nonLosAttacksKeysLabel = OutlineJLabel("Keys")
    private val nonLosAttacksMinRangeLabel = OutlineJLabel("Min range")
    private val nonLosAttacksMaxRangeLabel = OutlineJLabel("Max range")
    private val nonLosAttacksKeysTextField = JTextField()
    private val nonLosAttacksMinRangeTextField = IntegerJTextField()
    private val nonLosAttacksMaxRangeTextField = IntegerJTextField()

    private val gapCloserKeysLabel = OutlineJLabel("Keys")
    private val gapCloserMinRangeLabel = OutlineJLabel("Min range")
    private val gapCloserMaxRangeLabel = OutlineJLabel("Max range")
    private val gapCloserKeysTextField = JTextField()
    private val gapCloserMinRangeTextField = IntegerJTextField()
    private val gapCloserMaxRangeTextField = IntegerJTextField()

    private val mpBuffKeysLabel = OutlineJLabel("Keys")
    private val mpBuffAmountLabel = OutlineJLabel("Amount")
    private val mpBuffCdLabel = OutlineJLabel("CD")
    private val mpBuffKeysTextField = JTextField()
    private val mpBuffAmountTextField = IntegerJTextField()
    private val mpBuffCdTextField = IntegerJTextField()

    private val bigFont = Font("Impact", Font.PLAIN, 16)
    private val smallFont = Font("Impact", Font.PLAIN, 13)

    init {
        layout = null
        setSize(w, h)
        isOpaque = false

        val bigLabelDeltaW = width / 16
        val bigLabelDeltaH = height / 32
        val bigLabelW = width / 2 - bigLabelDeltaW
        val bigLabelH = 30
        val smallLabelDeltaW = bigLabelDeltaW / 2
        val smallLabelDeltaH = bigLabelDeltaH / 2
        val smallTextFieldW = (width / 2 - 2 * smallLabelDeltaW) / 3
        val smallLabelW = smallTextFieldW * 2
        val smallLabelH = 20

        losAttacksLabel.setBounds(bigLabelDeltaW, bigLabelDeltaH, bigLabelW, bigLabelH)
        losAttacksKeysLabel.setBounds(
            smallLabelDeltaW,
            smallLabelDeltaH + bigLabelDeltaH + bigLabelH,
            smallLabelW,
            smallLabelH
        )
        losAttacksMinRangeLabel.setBounds(
            smallLabelDeltaW,
            2 * smallLabelDeltaH + bigLabelDeltaH + bigLabelH + smallLabelH,
            smallLabelW,
            smallLabelH
        )
        losAttacksMaxRangeLabel.setBounds(
            smallLabelDeltaW,
            3 * smallLabelDeltaH + bigLabelDeltaH + bigLabelH + 2 * smallLabelH,
            smallLabelW,
            smallLabelH
        )
        losAttacksKeysTextField.setBounds(
            smallLabelDeltaW + smallLabelW,
            smallLabelDeltaH + bigLabelDeltaH + bigLabelH,
            smallTextFieldW,
            smallLabelH
        )
        losAttacksMinRangeTextField.setBounds(
            smallLabelDeltaW + smallLabelW,
            2 * smallLabelDeltaH + bigLabelDeltaH + bigLabelH + smallLabelH,
            smallTextFieldW,
            smallLabelH
        )
        losAttacksMaxRangeTextField.setBounds(
            smallLabelDeltaW + smallLabelW,
            3 * smallLabelDeltaH + bigLabelDeltaH + bigLabelH + 2 * smallLabelH,
            smallTextFieldW,
            smallLabelH
        )

        nonLosAttacksLabel.setBounds(width / 2 + bigLabelDeltaW, bigLabelDeltaH, bigLabelW, bigLabelH)
        nonLosAttacksKeysLabel.setBounds(
            width / 2 + smallLabelDeltaW,
            smallLabelDeltaH + bigLabelDeltaH + bigLabelH,
            smallLabelW,
            smallLabelH
        )
        nonLosAttacksMinRangeLabel.setBounds(
            width / 2 + smallLabelDeltaW,
            2 * smallLabelDeltaH + bigLabelDeltaH + bigLabelH + smallLabelH,
            smallLabelW,
            smallLabelH
        )
        nonLosAttacksMaxRangeLabel.setBounds(
            width / 2 + smallLabelDeltaW,
            3 * smallLabelDeltaH + bigLabelDeltaH + bigLabelH + 2 * smallLabelH,
            smallLabelW,
            smallLabelH
        )
        nonLosAttacksKeysTextField.setBounds(
            width / 2 + smallLabelDeltaW + smallLabelW,
            smallLabelDeltaH + bigLabelDeltaH + bigLabelH,
            smallTextFieldW,
            smallLabelH
        )
        nonLosAttacksMinRangeTextField.setBounds(
            width / 2 + smallLabelDeltaW + smallLabelW,
            2 * smallLabelDeltaH + bigLabelDeltaH + bigLabelH + smallLabelH,
            smallTextFieldW,
            smallLabelH
        )
        nonLosAttacksMaxRangeTextField.setBounds(
            width / 2 + smallLabelDeltaW + smallLabelW,
            3 * smallLabelDeltaH + bigLabelDeltaH + bigLabelH + 2 * smallLabelH,
            smallTextFieldW,
            smallLabelH
        )

        mpBuffLabel.setBounds(bigLabelDeltaW, height / 2 + bigLabelDeltaH, bigLabelW, bigLabelH)
        mpBuffKeysLabel.setBounds(
            smallLabelDeltaW,
            height / 2 + smallLabelDeltaH + bigLabelDeltaH + bigLabelH,
            smallLabelW,
            smallLabelH
        )
        mpBuffAmountLabel.setBounds(
            smallLabelDeltaW,
            height / 2 + 2 * smallLabelDeltaH + bigLabelDeltaH + bigLabelH + smallLabelH,
            smallLabelW,
            smallLabelH
        )
        mpBuffCdLabel.setBounds(
            smallLabelDeltaW,
            height / 2 + 3 * smallLabelDeltaH + bigLabelDeltaH + bigLabelH + 2 * smallLabelH,
            smallLabelW,
            smallLabelH
        )
        mpBuffKeysTextField.setBounds(
            smallLabelDeltaW + smallLabelW,
            height / 2 + smallLabelDeltaH + bigLabelDeltaH + bigLabelH,
            smallTextFieldW,
            smallLabelH
        )
        mpBuffAmountTextField.setBounds(
            smallLabelDeltaW + smallLabelW,
            height / 2 + 2 * smallLabelDeltaH + bigLabelDeltaH + bigLabelH + smallLabelH,
            smallTextFieldW,
            smallLabelH
        )
        mpBuffCdTextField.setBounds(
            smallLabelDeltaW + smallLabelW,
            height / 2 + 3 * smallLabelDeltaH + bigLabelDeltaH + bigLabelH + 2 * smallLabelH,
            smallTextFieldW,
            smallLabelH
        )

        gapCloserLabel.setBounds(width / 2 + bigLabelDeltaW, height / 2 + bigLabelDeltaH, bigLabelW, bigLabelH)
        gapCloserKeysLabel.setBounds(
            width / 2 + smallLabelDeltaW,
            height / 2 + smallLabelDeltaH + bigLabelDeltaH + bigLabelH,
            smallLabelW,
            smallLabelH
        )
        gapCloserMinRangeLabel.setBounds(
            width / 2 + smallLabelDeltaW,
            height / 2 + 2 * smallLabelDeltaH + bigLabelDeltaH + bigLabelH + smallLabelH,
            smallLabelW,
            smallLabelH
        )
        gapCloserMaxRangeLabel.setBounds(
            width / 2 + smallLabelDeltaW,
            height / 2 + 3 * smallLabelDeltaH + bigLabelDeltaH + bigLabelH + 2 * smallLabelH,
            smallLabelW,
            smallLabelH
        )
        gapCloserKeysTextField.setBounds(
            width / 2 + smallLabelDeltaW + smallLabelW,
            height / 2 + smallLabelDeltaH + bigLabelDeltaH + bigLabelH,
            smallTextFieldW,
            smallLabelH
        )
        gapCloserMinRangeTextField.setBounds(
            width / 2 + smallLabelDeltaW + smallLabelW,
            height / 2 + 2 * smallLabelDeltaH + bigLabelDeltaH + bigLabelH + smallLabelH,
            smallTextFieldW,
            smallLabelH
        )
        gapCloserMaxRangeTextField.setBounds(
            width / 2 + smallLabelDeltaW + smallLabelW,
            height / 2 + 3 * smallLabelDeltaH + bigLabelDeltaH + bigLabelH + 2 * smallLabelH,
            smallTextFieldW,
            smallLabelH
        )

        losAttacksLabel.font = bigFont
        losAttacksKeysLabel.font = smallFont
        losAttacksMinRangeLabel.font = smallFont
        losAttacksMaxRangeLabel.font = smallFont

        nonLosAttacksLabel.font = bigFont
        nonLosAttacksKeysLabel.font = smallFont
        nonLosAttacksMinRangeLabel.font = smallFont
        nonLosAttacksMaxRangeLabel.font = smallFont

        gapCloserLabel.font = bigFont
        gapCloserKeysLabel.font = smallFont
        gapCloserMinRangeLabel.font = smallFont
        gapCloserMaxRangeLabel.font = smallFont

        mpBuffLabel.font = bigFont
        mpBuffKeysLabel.font = smallFont
        mpBuffAmountLabel.font = smallFont
        mpBuffCdLabel.font = smallFont

        mpBuffLabel.foreground = Color.WHITE
        losAttacksLabel.foreground = Color.WHITE
        nonLosAttacksLabel.foreground = Color.WHITE
        gapCloserLabel.foreground = Color.WHITE
        losAttacksKeysLabel.foreground = Color.WHITE
        losAttacksMinRangeLabel.foreground = Color.WHITE
        losAttacksMaxRangeLabel.foreground = Color.WHITE
        nonLosAttacksKeysLabel.foreground = Color.WHITE
        nonLosAttacksMinRangeLabel.foreground = Color.WHITE
        nonLosAttacksMaxRangeLabel.foreground = Color.WHITE
        gapCloserKeysLabel.foreground = Color.WHITE
        gapCloserMinRangeLabel.foreground = Color.WHITE
        gapCloserMaxRangeLabel.foreground = Color.WHITE
        mpBuffKeysLabel.foreground = Color.WHITE
        mpBuffAmountLabel.foreground = Color.WHITE
        mpBuffCdLabel.foreground = Color.WHITE

        add(losAttacksLabel)
        add(losAttacksKeysLabel)
        add(losAttacksMinRangeLabel)
        add(losAttacksMaxRangeLabel)
        add(losAttacksKeysTextField)
        add(losAttacksMinRangeTextField)
        add(losAttacksMaxRangeTextField)

        add(mpBuffLabel)
        add(mpBuffKeysLabel)
        add(mpBuffAmountLabel)
        add(mpBuffCdLabel)
        add(mpBuffKeysTextField)
        add(mpBuffAmountTextField)
        add(mpBuffCdTextField)

        add(nonLosAttacksLabel)
        add(nonLosAttacksKeysLabel)
        add(nonLosAttacksMinRangeLabel)
        add(nonLosAttacksMaxRangeLabel)
        add(nonLosAttacksKeysTextField)
        add(nonLosAttacksMinRangeTextField)
        add(nonLosAttacksMaxRangeTextField)

        add(gapCloserLabel)
        add(gapCloserKeysLabel)
        add(gapCloserMinRangeLabel)
        add(gapCloserMaxRangeLabel)
        add(gapCloserKeysTextField)
        add(gapCloserMinRangeTextField)
        add(gapCloserMaxRangeTextField)
    }

    fun getLosAttacks(): RangeSpellCombination {
        return RangeSpellCombination(
            losAttacksKeysTextField.text,
            losAttacksMinRangeTextField.text.toInt(),
            losAttacksMaxRangeTextField.text.toInt()
        )
    }

    fun getNonLosAttacks(): RangeSpellCombination {
        return RangeSpellCombination(
            nonLosAttacksKeysTextField.text,
            nonLosAttacksMinRangeTextField.text.toInt(),
            nonLosAttacksMaxRangeTextField.text.toInt()
        )
    }

    fun getMpBuff(): BuffSpellCombination {
        return BuffSpellCombination(
            mpBuffKeysTextField.text,
            mpBuffAmountTextField.text.toInt(),
            mpBuffCdTextField.text.toInt()
        )
    }

    fun getGapCloser(): RangeSpellCombination {
        return RangeSpellCombination(
            gapCloserKeysTextField.text,
            gapCloserMinRangeTextField.text.toInt(),
            gapCloserMaxRangeTextField.text.toInt()
        )
    }

}