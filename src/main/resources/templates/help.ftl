<messageML>
    <#list commands>
        <table>
            <thead>
            <tr style="font-weight:bold">
                <td>Command</td>
                <td>Description</td>
            </tr>
            </thead>
            <tbody>
            <#items as item>
                <tr>
                    <td>${item.name()}</td>
                    <td>${item.description()}</td>
                </tr>
            </#items>
            </tbody>
        </table>
    <#else>
        <p>No commands are configured for this bot.</p>
    </#list>
</messageML>

