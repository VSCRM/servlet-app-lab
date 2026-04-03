import { api } from './api.js';
import { showGreeting } from './greeting.js';

export async function initProfile() {
	showGreeting();

	const box = document.getElementById('profile-data');

	const cookie = document.cookie.split(';').find(c => c.trim().startsWith('user_session='));
	if (!cookie) {
		box.innerHTML = `
            <div class="access-denied">
                <div class="access-denied__icon">🔒</div>
                <p>Cookie <code>user_session</code> відсутній.</p>
                <a href="login.html" class="btn btn-primary">Увійти</a>
            </div>`;
		return;
	}

	try {
		const d = await api.session.profile();
		box.innerHTML = `
            <table class="info-table">
                <tr><td>Користувач</td><td><code>${d.username}</code></td></tr>
                <tr><td>Session ID</td><td><code>${d.sessionId}</code></td></tr>
                <tr><td>Час сервера</td><td><code>${d.serverTime}</code></td></tr>
                <tr><td>IP-адреса</td><td><code>${d.remoteAddr}</code></td></tr>
                <tr><td>User-Agent</td><td><code>${d.userAgent}</code></td></tr>
            </table>`;
	} catch (err) {
		box.innerHTML = `<p class="error">Помилка: ${err.message}<br>
            <small>Переконайтесь що Tomcat запущено та servlet-legacy.war задеплоєно на порту 9000.</small></p>`;
	}
}
