let selectedParentId = null;


async function loadParentsPage() {

    const response = await fetch('/api/parents');

    if (!response.ok) {
        alert('Could not load parents.');
        return;
    }

    const parents = await response.json();

    const rows = document.getElementById('rows');

    if (parents.length === 0) {

        rows.innerHTML = `
            <tr>
                <td colspan="7" class="empty">
                    No parents registered.
                </td>
            </tr>
        `;

        return;
    }

    rows.innerHTML = parents.map(parent => {

        return `
            <tr>

                <td>
                    <b>${parent.parentCode}</b>
                </td>

                <td>
                    ${parent.fullName}
                </td>

                <td>
                    ${parent.phone}
                </td>

                <td>
                    ${parent.email || '—'}
                </td>

                <td>
                    ${parent.playerCount}
                </td>

                <td>
                    ${statusPill(parent.status)}
                </td>

                <td>

                    <button
                        class="icon-btn"
                        onclick="openPlayerLinkModal(
                            ${parent.id},
                            '${escapeHtml(parent.fullName)}'
                        )">

                        Manage Players

                    </button>

                </td>

            </tr>
        `;

    }).join('');
}


function openParentForm() {

    document
        .getElementById('parentModal')
        .classList
        .remove('hidden');
}


function closeParentForm() {

    document
        .getElementById('parentModal')
        .classList
        .add('hidden');
}


async function openPlayerLinkModal(
    parentId,
    parentName
) {

    selectedParentId = parentId;

    document.getElementById(
        'linkParentName'
    ).textContent =
        `Parent: ${parentName}`;


    document
        .getElementById('playerLinkModal')
        .classList
        .remove('hidden');


    const playerSelection =
        document.getElementById(
            'playerSelection'
        );

    playerSelection.innerHTML =
        'Loading players...';


    try {

        const response =
            await fetch('/api/players');


        if (!response.ok) {
            throw new Error(
                'Could not load players'
            );
        }


        const players =
            await response.json();


        if (players.length === 0) {

            playerSelection.innerHTML = `
                <div class="empty">
                    No players have been registered yet.
                </div>
            `;

            return;
        }


        playerSelection.innerHTML =
            players.map(player => {

                const linked =
                    player.parentId === parentId;


                return `
                    <label
                        style="
                            display:flex;
                            align-items:center;
                            gap:12px;
                            padding:12px;
                            border-bottom:1px solid #eee;
                            cursor:pointer;
                        "
                    >

                        <input
                            type="checkbox"
                            class="player-checkbox"
                            value="${player.id}"
                            ${linked ? 'checked' : ''}
                        >

                        <span>

                            <b>
                                ${player.fullName}
                            </b>

                            <small
                                style="
                                    display:block;
                                    opacity:.7;
                                "
                            >
                                ${player.playerCode}
                                ·
                                ${player.ageGroup}
                            </small>

                        </span>

                    </label>
                `;

            }).join('');


    } catch (error) {

        console.error(error);

        playerSelection.innerHTML = `
            <div class="empty">
                Could not load players.
            </div>
        `;
    }
}


function closePlayerLinkModal() {

    selectedParentId = null;

    document
        .getElementById('playerLinkModal')
        .classList
        .add('hidden');
}


async function savePlayerLinks() {

    if (!selectedParentId) {
        return;
    }


    const checkedPlayers =
        document.querySelectorAll(
            '.player-checkbox:checked'
        );


    const playerIds =
        Array.from(checkedPlayers)
            .map(
                checkbox =>
                    Number(checkbox.value)
            );


    const response = await fetch(
        `/api/parents/${selectedParentId}/players`,
        {
            method: 'PUT',

            headers: {
                'Content-Type':
                    'application/json'
            },

            body: JSON.stringify({
                playerIds: playerIds
            })
        }
    );


    if (!response.ok) {

        alert(
            'Could not save player links.'
        );

        return;
    }


    closePlayerLinkModal();

    await loadParentsPage();

    alert(
        'Player links saved successfully.'
    );
}


document
    .getElementById('parentForm')
    .addEventListener(
        'submit',
        async function (event) {

            event.preventDefault();


            const body = {

                fullName:
                document.getElementById(
                    'pFullName'
                ).value,

                phone:
                document.getElementById(
                    'pPhone'
                ).value,

                email:
                document.getElementById(
                    'pEmail'
                ).value,

                address:
                document.getElementById(
                    'pAddress'
                ).value,

                username:
                document.getElementById(
                    'pUsername'
                ).value,

                password:
                document.getElementById(
                    'pPassword'
                ).value
            };


            const response =
                await fetch(
                    '/api/parents',
                    {
                        method: 'POST',

                        headers: {
                            'Content-Type':
                                'application/json'
                        },

                        body:
                            JSON.stringify(body)
                    }
                );


            if (!response.ok) {

                alert(
                    'Could not save parent.'
                );

                return;
            }


            closeParentForm();

            event.target.reset();

            await loadParentsPage();
        }
    );


function escapeHtml(value) {

    return value
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}


document.addEventListener(
    'DOMContentLoaded',
    loadParentsPage
);