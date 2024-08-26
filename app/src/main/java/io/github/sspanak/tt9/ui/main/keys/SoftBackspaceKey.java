package io.github.sspanak.tt9.ui.main.keys;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import io.github.sspanak.tt9.R;
import io.github.sspanak.tt9.languages.LanguageKind;
import io.github.sspanak.tt9.preferences.settings.SettingsStore;
import io.github.sspanak.tt9.ui.Vibration;

public class SoftBackspaceKey extends SoftKey {
	private boolean hold;
	private int repeatCount = 0;

	public SoftBackspaceKey(Context context) {
		super(context);
	}

	public SoftBackspaceKey(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SoftBackspaceKey(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	final protected boolean handlePress() {
		super.handlePress();
		hold = false;
		repeatCount = 0;
		return validateTT9Handler() && deleteText();
	}

	@Override
	final protected void handleHold() {
		hold = true;

		if (
			validateTT9Handler()
			&& tt9.getSettings().getBackspaceAcceleration()
			&& ++repeatCount < SettingsStore.BACKSPACE_ACCELERATION_SOFT_KEY_REPEAT_DEBOUNCE
		) {
			return;
		}

		repeatCount = 0;
		deleteText();
	}

	@Override
	final protected boolean handleRelease() {
		vibrate(hold ? Vibration.getReleaseVibration() : Vibration.getNoVibration());
		hold = false;
		return true;
	}

	private boolean deleteText() {
		if (!tt9.onBackspace(hold && tt9.getSettings().getBackspaceAcceleration())) {
			// Limited or special numeric field (e.g. formatted money or dates) cannot always return
			// the text length, therefore onBackspace() seems them as empty and does nothing. This results
			// in fallback to the default hardware key action. Here we simulate the hardware BACKSPACE.
			tt9.sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL);
			return true;
		}

		return false;
	}

	@Override
	protected int getNoEmojiTitle() {
		return R.string.virtual_key_del;
	}

	@Override
	protected String getTitle() {
		return LanguageKind.isRTL(tt9 != null ? tt9.getLanguage() : null) ? "⌦" : "⌫";
	}

	@Override
	public void render() {
		super.render();
		if (tt9 != null) {
			setEnabled(!tt9.isVoiceInputActive());
		}
	}
}
