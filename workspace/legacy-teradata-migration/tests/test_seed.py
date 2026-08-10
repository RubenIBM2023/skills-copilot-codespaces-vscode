"""Pytest suite seeded for the OPF swarm.

Workers extend this suite with concrete tests defined by the active SPEC.
The single seed test below guarantees the test command exits with 0 even on
an empty workspace, so the HITL quality gate has a stable baseline.
"""


def test_workspace_seed() -> None:
    """Sanity check: the test runner is wired up correctly."""
    assert True
