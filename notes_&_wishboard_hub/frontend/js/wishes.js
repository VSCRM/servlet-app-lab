import { api } from './api.js';
import { toast } from './toast.js';

let currentUserId = null;

export async function initWishes() {
	try {
		const user = await api.session.profile();
		if (user.loggedIn && user.userId) {
			currentUserId = user.userId;
			await loadWishes();
			await loadStats();
		}
	} catch (err) {
		console.error(err);
	}

	document.getElementById('wish-form')?.addEventListener('submit', async e => {
		e.preventDefault();
		const name = document.getElementById('wish-name').value.trim();
		const desc = document.getElementById('wish-desc').value.trim();

		if (!name || !currentUserId) {
			toast('Введіть назву', 'error');
			return;
		}

		const btn = e.submitter;
		btn.disabled = true;
		btn.textContent = 'Додавання…';

		try {
			await api.wishes.create({ name, description: desc, userId: currentUserId });
			e.target.reset();
			const detailsTag = e.target.closest('details');
			if (detailsTag) detailsTag.removeAttribute('open');

			toast('Бажання додано ✓', 'success');
			await loadWishes();
			await loadStats();
		} catch (err) {
			toast('Помилка: ' + err.message, 'error');
		} finally {
			btn.disabled = false;
			btn.textContent = 'Додати';
		}
	});

	document.getElementById('wishes-list')?.addEventListener('click', async e => {
		const btn = e.target.closest('[data-action]');
		if (!btn) return;
		const id = Number(btn.dataset.id);

		if (btn.dataset.action === 'achieve') {
			btn.disabled = true;
			try {
				await api.wishes.achieve(id);
				toast('Виконано! 🎉', 'success');
				await loadWishes();
				await loadStats();
			} catch (err) {
				toast('Помилка: ' + err.message, 'error');
				btn.disabled = false;
			}
		}

		if (btn.dataset.action === 'delete-wish') {
			if (!confirm('Видалити бажання?')) return;
			btn.disabled = true;
			try {
				await api.wishes.remove(id);
				toast('Видалено', 'info');
				await loadWishes();
				await loadStats();
			} catch (err) {
				toast('Помилка: ' + err.message, 'error');
				btn.disabled = false;
			}
		}
	});
}

async function loadWishes() {
	const list = document.getElementById('wishes-list');
	if (!list || !currentUserId) return;

	list.innerHTML = '<p class="loading">Завантаження…</p>';
	try {
		const wishes = await api.wishes.getAll(currentUserId);
		if (!wishes.length) {
			list.innerHTML = '<p class="empty">Бажань немає</p>';
			return;
		}
		list.innerHTML = wishes.map(w => `
            <div class="wish-card ${w.achieved ? 'wish-card--done' : ''}">
                <div class="wish-card__body">
                    <strong>${esc(w.name)}</strong>
                    ${w.description ? `<p>${esc(w.description)}</p>` : ''}
                </div>
                <div class="wish-card__actions">
                    ${!w.achieved
				? `<button data-action="achieve" data-id="${w.id}" class="btn btn-success btn-sm">✓</button>`
				: '<span class="badge-done">Виконано</span>'}
                    <button data-action="delete-wish" data-id="${w.id}" class="btn btn-danger btn-sm">✕</button>
                </div>
            </div>
        `).join('');
	} catch (err) {
		list.innerHTML = `<p class="error">Помилка завантаження<br><small>${err.message}</small></p>`;
	}
}

async function loadStats() {
	try {
		const s = await api.wishes.getStats();
		const elTotal = document.getElementById('stat-total');
		const elAchieved = document.getElementById('stat-achieved');
		const elPending = document.getElementById('stat-pending');

		if (elTotal) elTotal.textContent = s.total ?? 0;
		if (elAchieved) elAchieved.textContent = s.achieved ?? 0;
		if (elPending) elPending.textContent = s.pending ?? 0;
	} catch (err) {
		console.error(err);
	}
}

function esc(s) {
	return String(s ?? '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}
