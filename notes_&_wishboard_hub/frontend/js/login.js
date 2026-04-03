import { api } from './api.js';
import { toast } from './toast.js';
import { showGreeting } from './greeting.js';

export function initLogin() {
	showGreeting();
	checkSession();

	document.getElementById('login-form')?.addEventListener('submit', async e => {
		e.preventDefault();
		const username = document.getElementById('username').value.trim();
		const password = document.getElementById('password').value;
		const errEl = document.getElementById('login-error');
		errEl.textContent = '';

		if (username.length < 3) {
			errEl.textContent = 'Мінімум 3 символи';
			return;
		}
		if (password.length < 4) {
			errEl.textContent = 'Мінімум 4 символи пароля';
			return;
		}

		const btn = e.submitter;
		btn.disabled = true;
		btn.textContent = 'Вхід…';

		try {
			const res = await api.session.login(username, password);
			if (res.ok) {
				toast(`Вітаємо, ${res.username}! Перенаправлення…`, 'success');
				await showGreeting();
				setTimeout(() => {
					window.location.href = 'index.html';
				}, 500);
			} else {
				errEl.textContent = res.error || 'Помилка входу';
			}
		} catch (err) {
			errEl.textContent = err.message;
			toast('Помилка: ' + err.message, 'error');
		} finally {
			btn.disabled = false;
			btn.textContent = 'Увійти';
		}
	});

	document.getElementById('logout-btn')?.addEventListener('click', async () => {
		try {
			await api.session.logout();
		} catch { }
		toast('Ви вийшли', 'info');
		setTimeout(() => location.reload(), 500);
	});
}

async function checkSession() {
	try {
		const data = await api.session.get();
		if (data && data.loggedIn) {
			document.getElementById('login-form').style.display = 'none';
			const loggedInBlock = document.getElementById('logged-in-block');
			if (loggedInBlock) {
				loggedInBlock.style.display = 'block';
			}
			const currentUserObj = document.getElementById('current-user');
			if (currentUserObj) {
				currentUserObj.textContent = data.username;
			}
		}
	} catch { }
}
