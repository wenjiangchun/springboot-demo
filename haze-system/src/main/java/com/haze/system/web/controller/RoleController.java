package com.haze.system.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.haze.system.entity.Resource;
import com.haze.system.entity.Role;
import com.haze.system.service.ResourceService;
import com.haze.system.service.RoleService;
import com.haze.system.utils.Status;
import com.haze.web.datatable.DataTablePage;
import com.haze.web.datatable.DataTableParams;
import com.haze.web.utils.WebMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * 角色管理Controller
 * @author sofar
 *
 */
@Controller
@RequestMapping(value = "/system/role")
public class RoleController {

	@Autowired
	private RoleService roleService;
	
	@Autowired
	private ResourceService resourceService;
	
	@RequestMapping(value = "view")
	public String list(Model model) {
		model.addAttribute("statuss", Status.values());
		return "system/role/roleList";
	}
	
	@RequestMapping(value = "search")
	@ResponseBody
	public DataTablePage search(DataTableParams dataTableParams) {
		PageRequest p = dataTableParams.getPageRequest();
		Map<String, Object> queryVaribles = dataTableParams.getQueryVairables();
		if (queryVaribles != null && queryVaribles.get("status") != null) {
			String value = (String) queryVaribles.get("status");
			queryVaribles.put("status", Status.valueOf(value));
		}
		Page<Role> roleList = this.roleService.findPage(p, dataTableParams.getQueryVairables());
		DataTablePage dtp = DataTablePage.generateDataTablePage(roleList, dataTableParams);
		return dtp;
	}

	@RequestMapping(value = "add", method = RequestMethod.GET)
	public String add(Model model) {
		model.addAttribute("statuss", Status.values());
		return "system/role/addRole";
	}
	
	@RequestMapping(value = "save")
    @ResponseBody
	public WebMessage save(Role role) {
		try {
			this.roleService.saveOrUpdate(role);
            return WebMessage.createSuccessWebMessage();
		} catch (Exception e) {
			return WebMessage.createErrorWebMessage(e.getMessage());
		}
	}
	
	@RequestMapping(value = "delete/{ids}")
    @ResponseBody
	public WebMessage delete(@PathVariable("ids") Long[] ids) {
        try {
            this.roleService.batchDelete(ids);
            return WebMessage.createSuccessWebMessage();
        } catch (Exception e) {
            return WebMessage.createErrorWebMessage(e.getMessage());
        }
	}
	
	/**
	 * 进入对角色添加资源管理权限页面
	 * @param id 角色Id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "addResources/{id}", method = RequestMethod.GET)
	public String addResources(@PathVariable("id") Long id, Model model) {
		Role role = this.roleService.findById(id);
		List<Resource> menus = this.resourceService.findMenuResources();
		List<Resource> newMenus = new ArrayList<Resource>();
		model.addAttribute("role",role);
		model.addAttribute("menus", menus);
		return "system/role/addResource";
	}
	
	/**
	 * 对角色添加资源管理权限
	 * @param id 角色Id
	 * @param resourceIds 系统资源Id数组
	 * @return 返回列表页面
	 */
	@RequestMapping(value = "saveResources/{roleId}/{resourceIds}")
    @ResponseBody
	public WebMessage saveResources(@PathVariable("roleId") Long id,@PathVariable("resourceIds") Long[] resourceIds) {
        try {
            this.roleService.addResources(id, resourceIds);
            return WebMessage.createSuccessWebMessage();
        } catch (Exception e) {
            return WebMessage.createErrorWebMessage(e.getMessage());
        }
    }
	
	/**
	 * 进入角色编辑页面
	 * @param id 用户Id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
	public String edit(@PathVariable Long id, Model model) {
		model.addAttribute("statuss", Status.values());
		Role role = this.roleService.findById(id);
		model.addAttribute("role", role);
		return "system/role/editRole";
	}
	
	/**
	 * 更新角色信息
	 * @param role 角色信息
	 * @return 返回角色列表页面
	 */
	@RequestMapping(value = "update")
    @ResponseBody
	public WebMessage update(Role role) {
		Role r = this.roleService.findById(role.getId());
		r.setName(role.getName());
		r.setEnabled(role.getEnabled());
		try {
			this.roleService.saveOrUpdate(r);
            /*return WebMessage.createLocaleSuccessWebMessage(RequestContextUtils.getLocale(request));*/
            return WebMessage.createSuccessWebMessage();
		} catch (Exception e) {
            return WebMessage.createErrorWebMessage(e.getMessage());
		}
	}
	
	/**
	 * 判断角色英文名是否存在
	 * @param roleName 角色英文名称
	 * @return true/false 不存在返回true,否则返回false
	 */
	@RequestMapping(value = "notExistRoleName", method = RequestMethod.POST)
	@ResponseBody
	public Boolean notExistRoleName(String roleName) {
		Boolean isExist = this.roleService.existCode(roleName);
		return !isExist;
	}
}
