(function () {
    'use strict';

    const COLORS = {
        primary: '#1B2A4A',
        success: '#16a34a',
        warning: '#eab308',
        /*warning: '#ea580c', ORANGE... */
        danger: '#dc2626',
        info: '#2563eb'
    };

    let chartEvolution, chartDonut, chartFiliere, chartInscriptions;

    function chargerDonnees(periode) {
        fetch('/dashboard/statistiques/data?periode=' + encodeURIComponent(periode))
            .then(function (res) {
                if (!res.ok) {
                    throw new Error('Erreur réseau : ' + res.status);
                }
                return res.json();
            })
            .then(function (data) {
                majEvolutionCandidatures(data.evolutionCandidatures);
                majRepartitionStatut(data.repartitionParStatut);
                majCandidaturesParFiliere(data.candidaturesParFiliere);
                majEvolutionInscriptions(data.evolutionInscriptions);
            })
            .catch(function (err) {
                console.error('Impossible de charger les statistiques :', err);
            });
    }

    function majEvolutionCandidatures(points) {
        const labels = points.map(function (p) { return p.label; });
        const valeurs = points.map(function (p) { return p.valeur; });

        if (chartEvolution) {
            chartEvolution.data.labels = labels;
            chartEvolution.data.datasets[0].data = valeurs;
            chartEvolution.update();
            return;
        }

        chartEvolution = new Chart(document.getElementById('chartEvolutionCandidatures'), {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Candidatures',
                    data: valeurs,
                    borderColor: COLORS.info,
                    backgroundColor: 'rgba(37, 99, 235, 0.1)',
                    fill: true,
                    tension: 0.35,
                    pointRadius: 3
                }]
            },
            options: {
                responsive: true,
                plugins: { legend: { display: false } },
                scales: { y: { beginAtZero: true } }
            }
        });
    }

    function majRepartitionStatut(points) {
        const labels = points.map(function (p) { return p.label; });
        const valeurs = points.map(function (p) { return p.valeur; });
        const total = valeurs.reduce(function (a, b) { return a + b; }, 0);
        const couleurs = [COLORS.success, COLORS.warning, COLORS.danger];

        if (chartDonut) {
            chartDonut.data.labels = labels;
            chartDonut.data.datasets[0].data = valeurs;
            chartDonut.update();
        } else {
            chartDonut = new Chart(document.getElementById('chartRepartitionStatut'), {
                type: 'doughnut',
                data: {
                    labels: labels,
                    datasets: [{
                        data: valeurs,
                        backgroundColor: couleurs,
                        borderWidth: 0
                    }]
                },
                options: {
                    responsive: true,
                    cutout: '68%',
                    plugins: { legend: { display: false } }
                },
                plugins: [{
                    id: 'centreTexte',
                    afterDraw: function (chart) {
                        const ctx = chart.ctx;
                        const totalActuel = chart.data.datasets[0].data.reduce(function (a, b) { return a + b; }, 0);
                        const w = chart.width, h = chart.height;
                        ctx.save();
                        ctx.font = '700 1.5rem Inter, sans-serif';
                        ctx.fillStyle = COLORS.primary;
                        ctx.textAlign = 'center';
                        ctx.textBaseline = 'middle';
                        ctx.fillText(totalActuel, w / 2, h / 2 - 8);
                        ctx.font = '400 0.75rem Inter, sans-serif';
                        ctx.fillStyle = '#64748b';
                        ctx.fillText('Total', w / 2, h / 2 + 14);
                        ctx.restore();
                    }
                }]
            });
        }

        const legende = document.getElementById('donutLegend');
        legende.innerHTML = '';
        labels.forEach(function (label, i) {
            const pourcentage = total > 0 ? Math.round((valeurs[i] * 100) / total) : 0;
            const li = document.createElement('li');
            li.innerHTML = '<span><span class="legend-dot" style="background:' + couleurs[i] + '"></span>' + label + '</span>' +
                '<strong>' + pourcentage + '% (' + valeurs[i] + ')</strong>';
            legende.appendChild(li);
        });
    }

    function majCandidaturesParFiliere(points) {
        const labels = points.map(function (p) { return p.label; });
        const valeurs = points.map(function (p) { return p.valeur; });

        if (chartFiliere) {
            chartFiliere.data.labels = labels;
            chartFiliere.data.datasets[0].data = valeurs;
            chartFiliere.update();
            return;
        }

        chartFiliere = new Chart(document.getElementById('chartCandidaturesParFiliere'), {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Candidatures',
                    data: valeurs,
                    backgroundColor: COLORS.info,
                    borderRadius: 4
                }]
            },
            options: {
                indexAxis: 'y',
                responsive: true,
                plugins: { legend: { display: false } },
                scales: { x: { beginAtZero: true } }
            }
        });
    }

    function majEvolutionInscriptions(points) {
        const labels = points.map(function (p) { return p.label; });
        const valeurs = points.map(function (p) { return p.valeur; });

        if (chartInscriptions) {
            chartInscriptions.data.labels = labels;
            chartInscriptions.data.datasets[0].data = valeurs;
            chartInscriptions.update();
            return;
        }

        chartInscriptions = new Chart(document.getElementById('chartEvolutionInscriptions'), {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Inscriptions',
                    data: valeurs,
                    borderColor: COLORS.success,
                    backgroundColor: 'rgba(22, 163, 74, 0.1)',
                    fill: true,
                    tension: 0.35,
                    pointRadius: 3
                }]
            },
            options: {
                responsive: true,
                plugins: { legend: { display: false } },
                scales: { y: { beginAtZero: true } }
            }
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        const select = document.getElementById('periodeSelect');
        chargerDonnees(select.value);

        select.addEventListener('change', function () {
            chargerDonnees(this.value);
        });
    });

})();