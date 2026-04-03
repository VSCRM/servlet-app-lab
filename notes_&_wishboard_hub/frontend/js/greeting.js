import { api } from './api.js';

export async function showGreeting() {
	const el = document.getElementById('greeting');
	if (!el) return;

	try {
		const data = await api.session.get();
		if (data && data.loggedIn) {
			el.textContent = `Вітаємо, ${data.username}! 👋`;
			el.classList.add('greeting--in');
		} else {
			el.textContent = 'Гість';
			el.classList.remove('greeting--in');
		}
	} catch (err) {
		el.textContent = 'Гість';
	}
}
