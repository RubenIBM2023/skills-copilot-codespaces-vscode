# Development Guidelines

This document outlines development standards and conventions for this project when working with OpenCode.

## Code Style & Quality

### Standards
- Follow language/framework conventions
- Keep functions small and focused
- Write self-documenting code
- Add comments for complex logic
- Use meaningful variable/function names

### Testing Requirements
- Write tests for new features
- Maintain >80% code coverage for critical paths
- Include both unit and integration tests
- Test edge cases and error conditions
- Update tests when behavior changes

### Documentation Standards
- Document public APIs with examples
- Update README when adding features
- Keep AGENTS.md current with conventions
- Document environment variables and configs
- Add comments to non-obvious code

## Working with Agents

### Using the Plan Agent
When planning features:
1. Provide context and requirements
2. Ask for a detailed implementation plan
3. Review and iterate on the proposal
4. Discuss trade-offs and alternatives

### Using the Build Agent
When implementing:
1. Reference the plan from the Plan agent
2. Ask to implement one logical unit at a time
3. Run tests after each major change
4. Ask for refactoring if code gets complex

### Using Subagents
- **@code-reviewer** — After feature completion
- **@security-auditor** — Before shipping to production
- **@docs-writer** — When adding new features
- **@explore** — To understand unfamiliar code
- **@general** — For complex multi-step tasks

## Commit Workflow

```
1. Make changes with Build agent
2. Ask Plan agent to review the diff
3. Address any feedback
4. Run full test suite
5. Commit with clear message
```

## Common Patterns

### Adding a New Feature

```
"Create a new feature that does X. Here's the context: [details]"
<Tab> "Plan out the implementation"
<Review and iterate>
<Tab> "Implement the changes"
"@code-reviewer Review the code"
"npm run test"
"git add . && git commit -m 'Add feature X'"
```

### Refactoring Code

```
"I want to refactor module X for better performance"
<Tab> "Suggest refactoring approach"
<Tab> "Implement the refactoring"
"npm run test"
"@code-reviewer Does this refactoring follow best practices?"
```

### Fixing a Bug

```
"There's a bug in X. Here's the error: [stack trace]"
<Tab> "Analyze the root cause"
<Tab> "Fix the bug"
"npm run test"
"git add . && git commit -m 'Fix bug in X'"
```

## Performance Considerations

- Avoid unnecessary API calls
- Cache computed values when appropriate
- Use pagination for large datasets
- Monitor memory usage in long-running processes
- Profile before optimizing

## Security Guidelines

- Never commit secrets (API keys, passwords, tokens)
- Use environment variables for sensitive config
- Validate all external input
- Follow OWASP guidelines for web applications
- Request security audit before production deployment

## Version Control

### Commit Messages
- Use imperative mood: "Add feature" not "Added feature"
- First line ≤ 50 characters
- Reference issue numbers when applicable
- Provide context in commit body for complex changes

### Branch Strategy
- Main branch: production-ready code
- Develop branch: integration point for features
- Feature branches: feature/feature-name
- Bug fix branches: fix/bug-name

## Debugging with OpenCode

```bash
# Ask Build agent to debug
"There's an issue with X. Here's what happens: [description]"

# Ask agent to add logging
"Add debug logging to help identify the issue"

# Ask for root cause analysis
"@explore Find where this error originates"
```

## Performance Profiling

```bash
# Ask agent to profile code
"Run profiling to identify performance bottlenecks in X"

# Analyze results
"Review these profiling results and suggest optimizations"
```

## Database Migrations

If using a database:
- Create migrations for schema changes
- Document migration purpose
- Test migrations locally first
- Never modify previous migrations
- Include rollback strategy

## API Documentation

For REST APIs:
- Document all endpoints with methods and paths
- Include request/response examples
- Document authentication requirements
- List possible error responses
- Include rate limiting info

## Dependencies

- Keep dependencies updated
- Review security advisories: `npm audit`
- Document why each dependency is needed
- Prefer established, well-maintained packages
- Evaluate dependency size impact

---

**Last updated**: 30 May 2026
